package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.MailService;
import ca.intelliware.ihtsdo.mlds.service.PasswordResetService;

import com.google.common.base.Strings;

@RestController
public class PasswordResetResource {
	
	@Resource
	MailService mailService;
	
	@Resource UserRepository userRepository;
	
	@Resource PasswordResetService passwordResetService;

	@RequestMapping(value=Routes.PASSWORD_RESET,
			method = RequestMethod.POST,
    		produces = "application/json")
	public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String,Object> params) {
		// validate email - bad request
		// look up user - not found?
		// send email
		String emailAddress = (String) params.get("email");
		Validate.notEmpty(emailAddress);
		
		User user = userRepository.getUserByEmail(emailAddress);
		String tokenKey = passwordResetService.createTokenForUser(user);
		
		// send email with token
		mailService.sendPasswordResetEmail(user, tokenKey);
		
		System.out.println("hello " + emailAddress);
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@RequestMapping(value=Routes.PASSWORD_RESET_ITEM,
			method = RequestMethod.POST,
    		produces = "application/json")
	public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody Map<String,Object>params) {
		
		String newPassword = (String) params.get("password");
		if (Strings.isNullOrEmpty(newPassword)) {
			return new ResponseEntity<>("no password provided", HttpStatus.BAD_REQUEST);
		}
		
		try {
			passwordResetService.resetPassword(token, newPassword);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>("Password reset token not found in our records", HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	
}
