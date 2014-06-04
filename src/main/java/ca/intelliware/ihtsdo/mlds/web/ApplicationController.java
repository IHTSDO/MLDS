package ca.intelliware.ihtsdo.mlds.web;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.registration.ApplicationRepository;

@Controller
public class ApplicationController {
	@Resource
	ApplicationRepository applicationRepository;
	@Resource
	SessionService sessionService;

	@RequestMapping(value="/api/applications",method=RequestMethod.POST)
	public Object createApplication(@RequestBody Map request) {
		Application application = new Application();
		application.setUsername(sessionService.getUsernameOrNull());
		application.setApproved(false);
		applicationRepository.save(application);
		
		return new Object();
	}
}
