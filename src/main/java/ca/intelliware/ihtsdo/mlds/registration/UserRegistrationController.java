package ca.intelliware.ihtsdo.mlds.registration;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.intelliware.ihtsdo.mlds.stormpath.StormpathApplication;

@Controller
public class UserRegistrationController {
	@Resource
	UserRegistrationService userRegistrationService;
	
	@Resource
	UserRegistrationRepository userRegistrationRepository;

	@RequestMapping("/registrations")
	public @ResponseBody Iterable<UserRegistration> getRegistrations() {
		return userRegistrationRepository.findAll();
	}
	
	@RequestMapping(value="/registrations/create",method=RequestMethod.POST)
	@ResponseStatus( HttpStatus.OK )
	public void createRegistration(@RequestParam String email, @RequestParam String name, @RequestParam String password) {
		
		StormpathApplication application = new StormpathApplication();
		application.createUser(email, password, name, name, name);
		
		userRegistrationService.createRegistration(email);
	}


}
