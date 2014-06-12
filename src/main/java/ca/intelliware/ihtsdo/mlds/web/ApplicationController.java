package ca.intelliware.ihtsdo.mlds.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.registration.ApplicationRepository;

@Controller
public class ApplicationController {
	@Resource
	ApplicationRepository applicationRepository;
	@Resource
	SessionService sessionService;

	@RequestMapping(value="api/applications")
	public @ResponseBody Iterable<Application> getApplications() {
		return applicationRepository.findAll();
	}
	
	@RequestMapping(value="api/applications/approve")
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
	
	@RequestMapping(value="/api/applications/create",method=RequestMethod.POST)
	public Object createApplication(@RequestBody JsonNode request) {
		JsonNode organization = request.get("organization");
		JsonNode contact =  request.get("contact");
		
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		Application application = new Application();
		
		if (applications.size() > 0) {
			application = applications.get(0);
		}
		
		application.setUsername(sessionService.getUsernameOrNull());
		application.setType(request.get("type").asText());
		application.setApplicantType(request.get("applicantType").asText());
		
		application.setName(contact.get("name").asText());
		application.setPhoneNumber(contact.get("phone").asText());

		if (request.get("applicantType").asText() == "Organisation") {
			application.setName(organization.get("name").asText());
			application.setAddress(organization.get("address").asText());
			application.setCity(organization.get("city").asText());
			application.setCountry(organization.get("country").asText());
			application.setPhoneNumber(organization.get("phonenumber").asText());
			application.setExtension(organization.get("extension").asText());
			application.setPosition(organization.get("position").asText());
			application.setWebsite(organization.get("website").asText());
		}
			
		application.setSnoMedLicence(request.get("snoMedTC").asBoolean());
		
		application.setApproved(false);
		applicationRepository.save(application);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
