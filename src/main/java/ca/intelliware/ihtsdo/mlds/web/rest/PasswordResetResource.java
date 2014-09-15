package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.intelliware.ihtsdo.mlds.domain.User;
import ca.intelliware.ihtsdo.mlds.repository.UserRepository;
import ca.intelliware.ihtsdo.mlds.service.PasswordResetService;
import ca.intelliware.ihtsdo.mlds.service.mail.MailService;
import ca.intelliware.ihtsdo.mlds.service.mail.PasswordResetEmailSender;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Strings;

@RestController
public class PasswordResetResource {
	
	@Resource
	MailService mailService;
	
	@Resource UserRepository userRepository;
	
	@Resource PasswordResetService passwordResetService;
	
	@Resource PasswordResetEmailSender passwordResetEmailSender;

	@RequestMapping(value=Routes.PASSWORD_RESET,
			method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
	@PermitAll
	@Timed
	public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String,Object> params) {
		String emailAddress = (String) params.get("email");
		if (Strings.isNullOrEmpty(emailAddress)) {
			return new ResponseEntity<>("no email provided", HttpStatus.BAD_REQUEST);
		}
		
		final User user = userRepository.getUserByEmail(emailAddress);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		final String tokenKey = passwordResetService.createTokenForUser(user);
		
		passwordResetEmailSender.sendPasswordResetEmail(user, tokenKey);
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@RequestMapping(value=Routes.PASSWORD_RESET_ITEM,
			method = RequestMethod.POST,
    		produces = MediaType.APPLICATION_JSON_VALUE)
	@PermitAll
	@Timed
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
