package ca.intelliware.ihtsdo.mlds.web;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.registration.ApplicationRepository;

@Controller
public class ApplicationController {
	@Resource
	ApplicationRepository applicationRepository;

	@RequestMapping(value="/api/application",method=RequestMethod.POST, consumes="application/json")
	public Object createApplication() {
		Application application = new Application();
		applicationRepository.save(application);
		
		return "OK";
	}
}
