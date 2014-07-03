package ca.intelliware.ihtsdo.mlds.web.rest;

import java.util.Locale;
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

import com.google.common.collect.Maps;

@RestController
public class PasswordResetResource {
	
	@Resource
	MailService mailService;
	
	@Resource TemplateEvaluator templateEvaluator;
	
	@Resource UserRepository userRepository;

	@RequestMapping(value=Routes.REQUEST_PASSWORD_RESET,
			method = RequestMethod.POST,
    		produces = "application/json")
	public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String,Object> params) {
		// validate email - bad request
		// look up user - not found?
		// send email
		String emailAddress = (String) params.get("email");
		Validate.notEmpty(emailAddress);
		
		// create and persist a reset token
		String token = "abc123";
		
		// send email with token
		Map<String, Object> variables = Maps.newHashMap();
		User user = userRepository.getUserByEmail(emailAddress);
		variables.put("user", user);
		variables.put("passwordResetUrl", templateEvaluator.getUrlBase() + "#/resetPassword?token="+token);
		String content = templateEvaluator.evaluateTemplate("passwordResetEmail", Locale.ENGLISH, variables);
		
		mailService.sendEmail(emailAddress, "subject", content, false, true);
		
		System.out.println("hello " + emailAddress);
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
	
	@RequestMapping(value=Routes.RESET_PASSWORD,
			method = RequestMethod.POST,
    		produces = "application/json")
	public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody Map<String,Object>params) {
		System.out.println("token " + token);
		System.out.println(params);
		
		if (!"abc123".equals(token)) {
			return new ResponseEntity<>("Token not found in our records", HttpStatus.NOT_FOUND);
		}
		
		// FIXME MLDS-20 suppress logging of password params?
		
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	
}
