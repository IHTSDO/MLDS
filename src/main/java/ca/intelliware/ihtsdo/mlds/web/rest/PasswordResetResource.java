package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.service.MailService;

@RestController
public class PasswordResetResource {
	
	@Resource
	MailService mailService;

	@RequestMapping(value=Routes.REQUEST_PASSWORD_RESET,
			method = RequestMethod.POST,
    		produces = "application/json")
	public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String,Object> params) {
		// validate email - bad request
		// look up user - not found?
		// send email
		String emailAddress = (String) params.get("email");
		Validate.notEmpty(emailAddress);
		mailService.sendEmail(emailAddress, "subject", "content", false, true);
		
		System.out.println("hello " + emailAddress);
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
}
