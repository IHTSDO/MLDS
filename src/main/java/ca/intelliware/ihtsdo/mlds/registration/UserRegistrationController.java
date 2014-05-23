package ca.intelliware.ihtsdo.mlds.registration;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserRegistrationController {
	@Resource
	UserRegistrationService userRegistrationService;
	
	@Resource
	UserRegistrationRepository userRegistrationRepository;

	@RequestMapping("/registrations/create")
	public void createRegistration(String email) {
		userRegistrationService.createRegistration(email);
	}

	@RequestMapping("/registrations")
	public @ResponseBody Iterable<UserRegistration> getRegistrations() {
		return userRegistrationRepository.findAll();
	}

}
