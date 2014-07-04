package ca.intelliware.ihtsdo.mlds.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.intelliware.ihtsdo.mlds.domain.Licensee;
import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.registration.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.LicenseeRepository;

import com.fasterxml.jackson.databind.JsonNode;

@Controller
public class ApplicationController {
	@Resource
	ApplicationRepository applicationRepository;
	@Resource
	SessionService sessionService;
	@Resource
	LicenseeRepository licenseeRepository;

	@RequestMapping(value="api/applications")
	public @ResponseBody Iterable<Application> getApplications() {
		return applicationRepository.findAll();
	}
	
	@RequestMapping(value="api/application/approve")
	public Object approveApplication(@RequestParam String email) {
		List<Application> applications = applicationRepository.findByUsername(email);
		if (applications.size() == 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Application application = applications.get(0);
		
		application.setApproved(true);
		applicationRepository.save(application);
		
		return new ResponseEntity<>(HttpStatus.OK);
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
		// Mark application as submitted
		application.setStatus();
		applicationRepository.save(application);
		
		//FIXME should be a different trigger and way to connect applications with licensee
		List<Licensee> licensees = licenseeRepository.findByCreator(application.getUsername());
		Licensee licensee = new Licensee();
		
		//FIXME only supporting 1 licensee for now
		if (licensees.size() > 0) {
			licensee = licensees.get(0);
		}
		licensee.setCreator(application.getUsername());
		licensee.setApplication(application);
		licensee.setType(application.getType());
		licenseeRepository.save(licensee);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/api/application/save",method=RequestMethod.POST)
	public Object saveApplication(@RequestBody JsonNode request) {
		
        Application application = saveApplicationFields(request);
		// Mark application as not submitted
		application.resetStatus();
		
		applicationRepository.save(application);
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
		
		application.setApproved(false);
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
