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
import ca.intelliware.ihtsdo.mlds.domain.LicenseeType;
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
	
	@RequestMapping(value="/api/application/create",method=RequestMethod.POST)
	public Object createApplication(@RequestBody JsonNode request) {
		System.out.println("in call " + request);
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
		
		application.setType(LicenseeType.valueOf(request.get("type").asText().toUpperCase()));
		application.setSubType(request.get("usageSubType").asText());
		
		application.setName(contact.get("name").asText());
		application.setPhoneNumber(contact.get("phone").asText());
		application.setMobileNumber(contact.get("mobilePhone").asText());
		application.setEmail(contact.get("email").asText());
		
		application.setAddress(address.get("street").asText());
		application.setCity(address.get("city").asText());
		application.setCountry(address.get("country").asText());
		
		// Optional Fields
		application.setExtension(setField(contact, "extenstion"));
		application.setAlternateEmail(setField(contact, "alternateEmail"));
		application.setThirdEmail(setField(contact, "thirdEmail"));
		
		application.setOrganizationName(setField(organization, "name"));
		application.setOrganizationType(setField(organization, "type"));
		
		application.setBillingStreet(setField(billing, "street"));
		application.setBillingCity(setField(billing, "city"));
		application.setBillingCountry(setField(billing, "country"));
		
		application.setOtherText(setField(request, "otherText"));

		// FIXME MB map unset to false?
		application.setSnoMedLicence(request.get("snoMedTC").asBoolean());
		
		// TODO: add flag to indicate that application is not submitted yet
		
		application.setApproved(true);
		applicationRepository.save(application);
		
		//FIXME should be a different trigger and way to connect applications with licensee
		Licensee licensee = new Licensee();
		licensee.setCreator(application.getUsername());
		licensee.setApplication(application);
		licensee.setType(application.getType());
		licenseeRepository.save(licensee);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	private String setField(JsonNode jsonNode, String attribute) {
		if (jsonNode.get(attribute) != null) {
			return jsonNode.get(attribute).asText();
		}
		return null;
	}
}
