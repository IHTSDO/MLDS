package ca.intelliware.ihtsdo.mlds.web;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

import org.joda.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.Licensee;
import ca.intelliware.ihtsdo.mlds.domain.LicenseeType;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.registration.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.LicenseeRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.Routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

@Controller
public class ApplicationController {
	@Resource
	ApplicationRepository applicationRepository;
	@Resource
	SessionService sessionService;
	@Resource
	LicenseeRepository licenseeRepository;
	@Resource
	ApplicationApprovedEmailSender applicationApprovedEmailSender;
	@Resource
	UserRepository userRepository;
	@Resource
	ApplicationAuditEvents applicationAuditEvents;

	@RequestMapping(value="api/applications")
	public @ResponseBody Iterable<Application> getApplications() {
		return applicationRepository.findAll();
	}
	
	@RequestMapping(value="/api/application/{applicationId}/approve",
			method=RequestMethod.POST,
			produces = "application/json")
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public @ResponseBody ResponseEntity<Application> approveApplication(@PathVariable long applicationId, @RequestBody String approvalStateString) {
		//FIXME why cant this be the body type?
		ApprovalState approvalState = ApprovalState.valueOf(approvalStateString);
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		//FIXME should there be state transition validation?
		application.setApprovalState(approvalState);
		
		//FIXME add flags to approval state?
		if (Objects.equal(approvalState, ApprovalState.APPROVED) || Objects.equal(approvalState, ApprovalState.REJECTED)) {
			application.setCompletedAt(Instant.now());
		}
		applicationRepository.save(application);
		
		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			User user = userRepository.getUserByEmail(application.getEmail());
			applicationApprovedEmailSender.sendApplicationApprovalEmail(user);
		}
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}
	
	@RequestMapping(value = Routes.APPLICATIONS, 
			method=RequestMethod.GET,
			produces = "application/json")
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public @ResponseBody ResponseEntity<Collection<Application>> getApplications(@RequestParam(value="$filter") String filter){
		Iterable<Application> applications;
		if (filter == null) {
			applications = applicationRepository.findAll();
		} else {
			// Limited OData implementation - expand or use real OData library in the future
			if (Objects.equal(filter, "approvalState/pending eq true")) {
				applications = applicationRepository.findByApprovalStateIn(Lists.newArrayList(ApprovalState.SUBMITTED, ApprovalState.RESUBMITTED, ApprovalState.REVIEW_REQUESTED));
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<Collection<Application>>(Lists.newArrayList(applications), HttpStatus.OK);
	}

	@RequestMapping(value="/api/application", method=RequestMethod.GET)
	public Object getUserApplication(){
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		if (applications.size() > 0) {
			return new ResponseEntity<Application>(applications.get(0), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/api/application/submit",method=RequestMethod.POST)
	public Object submitApplication(@RequestBody JsonNode request) {
		Application application = saveApplicationFields(request);
		ApprovalState preApprovalState = application.getApprovalState();
		// Mark application as submitted
		if (Objects.equal(application.getApprovalState(), ApprovalState.CHANGE_REQUESTED)
				|| Objects.equal(application.getApprovalState(), ApprovalState.RESUBMITTED)) {
			application.setApprovalState(ApprovalState.RESUBMITTED);
		} else {
			application.setApprovalState(ApprovalState.SUBMITTED);
		}
		application.setSubmittedAt(Instant.now());
		applicationRepository.save(application);
		
		if (!Objects.equal(application.getApprovalState(), preApprovalState)) {
			applicationAuditEvents.logApprovalStateChange(application);
		}
		
		//FIXME should be a different trigger and way to connect applications with licensee
		List<Licensee> licensees = licenseeRepository.findByCreator(application.getUsername());
		Licensee licensee = new Licensee();
		
		//FIXME only supporting 1 licensee for now
		if (licensees.size() > 0) {
			licensee = licensees.get(0);
		}
		licensee.setCreator(application.getUsername());
		licensee.setApplication(application);
		licensee.setType(LicenseeType.valueOf(application.getType()));
		licenseeRepository.save(licensee);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	//FIXME mismatch of API styles with rest of this class...
	@RequestMapping(value="/api/application/{applicationId}/notesInternal",
			method=RequestMethod.PUT,
			produces = "application/json")
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public @ResponseBody ResponseEntity<Application> submitApplication(@PathVariable long applicationId, @RequestBody String notesInternal) {
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		application.setNotesInternal(notesInternal);
		applicationRepository.save(application);
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	
	@RequestMapping(value="/api/application/save",method=RequestMethod.POST)
	public Object saveApplication(@RequestBody JsonNode request) {
		
        Application application = saveApplicationFields(request);
        ApprovalState preApprovalState = application.getApprovalState();
		// Mark application as not submitted
		if (Objects.equal(application.getApprovalState(), ApprovalState.CHANGE_REQUESTED)
				|| Objects.equal(application.getApprovalState(), ApprovalState.RESUBMITTED)) {
			application.setApprovalState(ApprovalState.CHANGE_REQUESTED);
		} else {
			application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		}
		
		if (!Objects.equal(application.getApprovalState(), preApprovalState)) {
			applicationRepository.save(application);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Application saveApplicationFields(JsonNode request) {
		JsonNode organization = request.get("organization");
        JsonNode contact = request.get("contact");
        JsonNode address = request.get("address");
        JsonNode billing = request.get("billing");

		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		Application application = new Application();
		
		if (applications.size() > 0) {
			application = applications.get(0);
		}
		
		application.setUsername(sessionService.getUsernameOrNull());
		application.setType(setField(request, "type"));
		application.setSubType(setField(request, "usageSubType"));
		
		application.setName(setField(contact, "name"));
		application.setPhoneNumber(setField(contact, "phone"));
		application.setMobileNumber(setField(contact, "mobilePhone"));
		application.setEmail(setField(contact, "email"));
		
		application.setAddress(setField(address, "street"));
		application.setCity(setField(address, "city"));
		application.setPostCode(setField(address, "postCode"));
		application.setCountry(setField(address, "country"));
		
		application.setExtension(setField(contact, "extension"));
		application.setAlternateEmail(setField(contact, "alternateEmail"));
		application.setThirdEmail(setField(contact, "thirdEmail"));
		
		application.setOrganizationName(setField(organization, "name"));
		application.setOrganizationType(setField(organization, "type"));
		application.setOrganizationTypeOther(setField(organization, "typeOther"));
		
		application.setBillingStreet(setField(billing, "street"));
		application.setBillingCity(setField(billing, "city"));
		application.setBillingPostCode(setField(billing, "postCode"));
		application.setBillingCountry(setField(billing, "country"));
		
		application.setOtherText(setField(request, "otherText"));

		// FIXME MB map unset to false?
		application.setSnoMedLicence(Boolean.parseBoolean(setField(request, "snoMedTC")));
		
		if (application.getApprovalState() == null) {
			application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		}
		return application;
	}
	
	private String setField(JsonNode jsonNode, String attribute) {
		if (jsonNode.get(attribute) != null) {
			if(jsonNode.get(attribute).asText() != "") {
				return jsonNode.get(attribute).asText();
			}
		}
		return new String();
	}
}
