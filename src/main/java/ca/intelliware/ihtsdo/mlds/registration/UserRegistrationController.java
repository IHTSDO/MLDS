package ca.intelliware.ihtsdo.mlds.registration;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.intelliware.ihtsdo.mlds.stormpath.StormpathApplication;

import com.stormpath.sdk.resource.ResourceException;

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
	
	@RequestMapping("/reset-password")
	public Object resetPassword(@RequestParam String email) {
		UserRegistration user = userRegistrationRepository.findByEmail(email);
		
		if (user != null) {
			StormpathApplication application = new StormpathApplication();
			application.resetPassword(email);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/registrations/create",method=RequestMethod.POST)
	@ResponseStatus( HttpStatus.OK )
	public ResponseEntity<ResourceException> createRegistration(@RequestParam String email, @RequestParam String name, @RequestParam String password) {
		StormpathApplication application = new StormpathApplication();
		
		try {
			application.createUser(email, password, name, name, name);
			userRegistrationService.createRegistration(email);
		} catch (ResourceException error){
			
			ResponseEntity<ResourceException> errorMessage = new ResponseEntity<ResourceException>(error , HttpStatus.BAD_REQUEST);
			return errorMessage;
		}
		return null;
		
	}


}
