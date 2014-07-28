package ca.intelliware.ihtsdo.mlds.web;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;

import org.joda.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateSubType;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateType;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.MailingAddress;
import ca.intelliware.ihtsdo.mlds.domain.OrganizationType;
import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateDetailsRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.CountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.service.AffiliateDetailsResetter;
import ca.intelliware.ihtsdo.mlds.service.mail.ApplicationApprovedEmailSender;
import ca.intelliware.ihtsdo.mlds.web.rest.ApplicationAuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.web.rest.Routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

@RestController
public class ApplicationController {
	@Resource
	ApplicationRepository applicationRepository;
	@Resource
	SessionService sessionService;
	@Resource
	AffiliateRepository affiliateRepository;
	@Resource
	ApplicationApprovedEmailSender applicationApprovedEmailSender;
	@Resource
	UserRepository userRepository;
	@Resource
	ApplicationAuditEvents applicationAuditEvents;
	@Resource
	ApplicationAuthorizationChecker authorizationChecker;
	@Resource
	CountryRepository countryRepository;
	@Resource
	AffiliateDetailsRepository affiliateDetailsRepository;
	@Resource
	AffiliateDetailsResetter affiliateDetailsResetter;

	@RequestMapping(value="api/applications")
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	public @ResponseBody Iterable<Application> getApplications() {
		return applicationRepository.findAll();
	}
	
	@RequestMapping(value = Routes.APPLICATION_APPROVE,
			method=RequestMethod.POST,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	public @ResponseBody ResponseEntity<Application> approveApplication(@PathVariable long applicationId, @RequestBody String approvalStateString) throws CloneNotSupportedException {
		//FIXME why cant this be the body type?
		ApprovalState approvalState = ApprovalState.valueOf(approvalStateString);
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		authorizationChecker.checkCanApproveApplication(application);
		
		//FIXME should there be state transition validation?
		application.setApprovalState(approvalState);
		
		//FIXME add flags to approval state?
		if (Objects.equal(approvalState, ApprovalState.APPROVED) || Objects.equal(approvalState, ApprovalState.REJECTED)) {
			application.setCompletedAt(Instant.now());
		}
		
		
		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			List<Affiliate> affiliates = affiliateRepository.findByCreator(application.getUsername());
			
			if (affiliates.size() > 0) {
				Affiliate affiliate = affiliates.get(0);
				AffiliateDetails affiliateDetails = (AffiliateDetails) application.getAffiliateDetails().clone(); 
				
				affiliateDetailsResetter.detach(affiliateDetails);
				
				affiliateDetails = affiliateDetailsRepository.save(affiliateDetails);
				affiliate.setAffiliateDetails(affiliateDetails);
				affiliateRepository.save(affiliate);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
		
		
		
		applicationRepository.save(application);
		
		if (Objects.equal(approvalState, ApprovalState.APPROVED)) {
			User user = userRepository.getUserByEmail(application.getAffiliateDetails().getEmail());
			applicationApprovedEmailSender.sendApplicationApprovalEmail(user);
		}
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}
	
	@RequestMapping(value = Routes.APPLICATIONS, 
			method=RequestMethod.GET,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
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

	@RequestMapping(value = Routes.APPLICATION, 
			method=RequestMethod.GET,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN})
	public  @ResponseBody ResponseEntity<Application> getApplication(@PathVariable long applicationId){
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		authorizationChecker.checkCanAccessApplication(application);
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	@RequestMapping(value = Routes.APPLICATION_ME, 
			method=RequestMethod.GET,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.USER})
	public  @ResponseBody ResponseEntity<Application> getApplicationForMe(){
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		if (applications.size() > 0) {
			return new ResponseEntity<Application>(applications.get(0), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Transactional
	@RequestMapping(value="/api/application", 
			method=RequestMethod.GET,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.USER})
	public @ResponseBody ResponseEntity<Application> getUserApplication(){
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		if (applications.size() > 0) {
			return new ResponseEntity<Application>(applications.get(0), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value=Routes.APPLICATION_REGISTRATION,
			method=RequestMethod.POST,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.USER})
	public @ResponseBody ResponseEntity<Application> submitApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
		Application application = findOrStartInitialApplication();
        application = saveApplicationFields(request,application);
		authorizationChecker.checkCanAccessApplication(application);
		
		// Mark application as submitted
		if (Objects.equal(application.getApprovalState(), ApprovalState.CHANGE_REQUESTED)) {
			application.setApprovalState(ApprovalState.RESUBMITTED);
		} else if (Objects.equal(application.getApprovalState(), ApprovalState.NOT_SUBMITTED)){
			application.setApprovalState(ApprovalState.SUBMITTED);
		} else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		application.setSubmittedAt(Instant.now());
		applicationRepository.save(application);
		
		applicationAuditEvents.logApprovalStateChange(application);
		
		//FIXME should be a different trigger and way to connect applications with affiliate
		List<Affiliate> affiliates = affiliateRepository.findByCreator(application.getUsername());
		Affiliate affiliate = new Affiliate();
		
		//FIXME only supporting 1 affiliate for now
		if (affiliates.size() > 0) {
			affiliate = affiliates.get(0);
		}
		affiliate.setCreator(application.getUsername());
		affiliate.setApplication(application);
		affiliate.setType(application.getType());
		affiliateRepository.save(affiliate);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	
	@RequestMapping(value=Routes.APPLICATION_REGISTRATION,
			method=RequestMethod.PUT,
			produces = "application/json")
	@RolesAllowed({AuthoritiesConstants.USER})
	public @ResponseBody ResponseEntity<Application> saveApplication(@PathVariable long applicationId, @RequestBody JsonNode request) {
        Application application = findOrStartInitialApplication();
        application = saveApplicationFields(request,application);
        ApprovalState preApprovalState = application.getApprovalState();
		// Mark application as not submitted
		if (Objects.equal(application.getApprovalState(), ApprovalState.CHANGE_REQUESTED)) {
			application.setApprovalState(ApprovalState.CHANGE_REQUESTED);
		} else {
			application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		}
		
		if (!Objects.equal(application.getApprovalState(), preApprovalState)) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		applicationRepository.save(application);
		
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}

	@Transactional
	@RequestMapping(value = Routes.APPLICATION_NOTES_INTERNAL,
			method=RequestMethod.PUT,
			produces = "application/json")
	@RolesAllowed({ AuthoritiesConstants.STAFF, AuthoritiesConstants.ADMIN })
	public @ResponseBody ResponseEntity<Application> submitApplication(@PathVariable long applicationId, @RequestBody String notesInternal) {
		Application application = applicationRepository.findOne(applicationId);
		if (application == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		application.setNotesInternal(notesInternal);
		applicationRepository.save(application);
		return new ResponseEntity<Application>(application, HttpStatus.OK);
	}


	private Application saveApplicationFields(JsonNode request,Application application) {
        JsonNode affiliateDetailsJsonNode = request.get("affiliateDetails");
        JsonNode address = affiliateDetailsJsonNode.get("address");
        JsonNode billing = affiliateDetailsJsonNode.get("billingAddress");

		application.setUsername(sessionService.getUsernameOrNull());
		
		if (checkIfValidField(request, "type")){
			application.setType(AffiliateType.valueOf(getStringField(request, "type")));
		}
		if (checkIfValidField(request, "subType")) {
			application.setSubType(AffiliateSubType.valueOf(getStringField(request, "subType")));
		}
		
		AffiliateDetails affiliateDetails = application.getAffiliateDetails();
		createAffiliateDetails(affiliateDetailsJsonNode, address, billing, affiliateDetails);
		affiliateDetailsRepository.save(affiliateDetails);
		application.setAffiliateDetails(affiliateDetails);
		
		application.setOtherText(getStringField(request, "otherText"));

		application.setSnoMedLicence(Boolean.parseBoolean(getStringField(request, "snoMedTC")));
		
		if (application.getApprovalState() == null) {
			application.setApprovalState(ApprovalState.NOT_SUBMITTED);
		}
		return application;
	}

	private Application findOrStartInitialApplication() {
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		Application application = new Application();
		
		if (applications.size() > 0) {
			application = applications.get(0);
		}
		return application;
	}
	
	private void createAffiliateDetails(JsonNode affiliateDetailsJsonNode, JsonNode addressJsonNode, JsonNode billingJsonNode, AffiliateDetails affiliateDetails) {
		if (affiliateDetails == null) {
			affiliateDetails = new AffiliateDetails();
		}
		
		affiliateDetails.setFirstName(getStringField(affiliateDetailsJsonNode, "firstName"));
		affiliateDetails.setLastName(getStringField(affiliateDetailsJsonNode, "lastName"));
		affiliateDetails.setLandlineNumber(getStringField(affiliateDetailsJsonNode, "landlineNumber"));
		affiliateDetails.setLandlineExtension(getStringField(affiliateDetailsJsonNode, "landlineExtension"));
		affiliateDetails.setMobileNumber(getStringField(affiliateDetailsJsonNode, "mobileNumber"));
		affiliateDetails.setEmail(getStringField(affiliateDetailsJsonNode, "email"));
		affiliateDetails.setAlternateEmail(getStringField(affiliateDetailsJsonNode, "alternateEmail"));
		affiliateDetails.setThirdEmail(getStringField(affiliateDetailsJsonNode, "thirdEmail"));
		affiliateDetails.setOrganizationName(getStringField(affiliateDetailsJsonNode, "organizationName"));
		affiliateDetails.setOrganizationTypeOther(getStringField(affiliateDetailsJsonNode, "organizationTypeOther"));
		
		if (checkIfValidField(affiliateDetailsJsonNode, "organizationType")) {
			affiliateDetails.setOrganizationType(OrganizationType.valueOf(getStringField(affiliateDetailsJsonNode, "organizationType")));
		}
		
		MailingAddress mailingAddress = new MailingAddress();
		
		mailingAddress.setStreet(getStringField(addressJsonNode, "street"));
		mailingAddress.setCity(getStringField(addressJsonNode, "city"));
		mailingAddress.setPost(getStringField(addressJsonNode, "post"));
		
		JsonNode country = addressJsonNode.get("country");
		if (checkIfValidField(country, "isoCode2")) {
			mailingAddress.setCountry(countryRepository.findOne(getStringField(country, "isoCode2")));
		}
		
		affiliateDetails.setAddress(mailingAddress);
		
		MailingAddress billingAddress = new MailingAddress();
		
		billingAddress.setStreet(getStringField(billingJsonNode, "street"));
		billingAddress.setCity(getStringField(billingJsonNode, "city"));
		billingAddress.setPost(getStringField(billingJsonNode, "post"));
		JsonNode billingCountry = billingJsonNode.get("country");
		if (checkIfValidField(billingCountry, "isoCode2")) {
			billingAddress.setCountry(countryRepository.findOne(getStringField(billingCountry, "isoCode2")));
		}
		
		affiliateDetails.setBillingAddress(billingAddress);
	}
	
	private String getStringField(JsonNode jsonNode, String attribute) {
		if (checkIfValidField(jsonNode, attribute)) {
			return jsonNode.get(attribute).asText();
		}
		return new String();
	}
	
	private Boolean checkIfValidField(JsonNode jsonNode, String attribute) {
		if (jsonNode != null && jsonNode.get(attribute) != null) {
			if(jsonNode.get(attribute).asText() != "" && jsonNode.get(attribute).asText() != "null") {
				return true;
			}
		}
		return false;
	}
}
