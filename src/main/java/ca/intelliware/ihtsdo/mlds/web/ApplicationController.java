package ca.intelliware.ihtsdo.mlds.web;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public Object approveApplication() {
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		if (applications.size() == 0) {
			new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Application application = applications.get(0);
		
		application.setApproved(true);
		applicationRepository.save(application);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/api/applications/create",method=RequestMethod.POST)
	public Object createApplication(@RequestBody Map request) {
		Map<String, String> organization = (Map<String, String>) request.get("organization");
		
		List<Application> applications = applicationRepository.findByUsername(sessionService.getUsernameOrNull());
		Application application = new Application();
		
		if (applications.size() > 0) {
			application = applications.get(0);
		}
		
		application.setUsername(sessionService.getUsernameOrNull());
		application.setType((String) request.get("type"));
		
		application.setName(organization.get("name"));
		application.setAddress(organization.get("address"));
		application.setCity(organization.get("city"));
		application.setCountry(organization.get("country"));
		application.setPhoneNumber(organization.get("phonenumber"));
		application.setExtension(organization.get("extension"));
		application.setPosition(organization.get("position"));
		application.setWebsite(organization.get("website"));
		
		application.setSnoMedLicence((boolean) request.get("snoMedTC"));
		
		application.setApproved(false);
		applicationRepository.save(application);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
