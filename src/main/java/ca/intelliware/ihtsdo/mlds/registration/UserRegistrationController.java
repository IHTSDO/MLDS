package ca.intelliware.ihtsdo.mlds.registration;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

@Controller
public class UserRegistrationController {
	@Resource
	UserRegistrationService userRegistrationService;

	public void createRegistration(String email) {
		userRegistrationService.createRegistration(email);
	}
	
	

}
