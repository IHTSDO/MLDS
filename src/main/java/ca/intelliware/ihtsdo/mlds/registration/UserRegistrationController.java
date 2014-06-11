package ca.intelliware.ihtsdo.mlds.registration;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.intelliware.ihtsdo.commons.event.EventRepository;
import ca.intelliware.ihtsdo.commons.event.model.user.RegisterNewUserEvent;
import ca.intelliware.ihtsdo.commons.event.model.user.RegistrationEventType;
import ca.intelliware.ihtsdo.mlds.stormpath.StormpathApplication;
import ca.intelliware.ihtsdo.mlds.web.WebAttributesService;

import com.google.common.collect.Maps;
import com.stormpath.sdk.resource.ResourceException;

@Controller
public class UserRegistrationController {
	@Resource
	UserRegistrationService userRegistrationService;
	
	@Resource
	UserRegistrationRepository userRegistrationRepository;
	
	@Resource
	EventRepository eventRepository;
	
	@Resource
	WebAttributesService eventService;

	// FIXME: JH have new registration controller use this
	@Resource
	DomainBlacklistService domainBlacklistService;

	// FIXME: JH-update angular so that admin dashboard can see registrations
	@RequestMapping("/registrations")
	public @ResponseBody Iterable<UserRegistration> getRegistrations() {
		return userRegistrationRepository.findAll();
	}
	
	// FIXME: JH-replace password handling with stormpath replacement
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
	public ResponseEntity<?> createRegistration(@RequestParam String email, @RequestParam String name, @RequestParam String password, @RequestParam boolean tos) {
		StormpathApplication application = new StormpathApplication();
		
		// FIXME MB this needs to go down into service, and we need an exception layer.
		if (!tos) {
			Map<String,Object> errors = Maps.newHashMap();
			// FIXME MB just emulating the SP exception until I re-write this.
			errors.put("code","");
			errors.put("developerMessage","The TOS terms must be accepted to continue");
			return new ResponseEntity<Object>(errors,HttpStatus.FORBIDDEN);
		}
		
		// FIXME AC to be added to the exception layer
		if(domainBlacklistService.isDomainBlacklisted(email)) {
			Map<String,Object> errors = Maps.newHashMap();
			errors.put("code","");
			errors.put("developerMessage","The email domain used is not accepted.");
			return new ResponseEntity<Object>(errors,HttpStatus.FORBIDDEN);
		}
		
		try {
			application.createUser(email, password, name, name, name);
			userRegistrationService.createRegistration(email);
			eventRepository.save(new RegisterNewUserEvent(RegistrationEventType.TOS_AGREEMENT,email,eventService.getCurrentRequestWebAttributes()));
		} catch (ResourceException error){
			
			ResponseEntity<ResourceException> errorMessage = new ResponseEntity<ResourceException>(error , HttpStatus.BAD_REQUEST);
			return errorMessage;
		}
		return null;
		
	}


}
