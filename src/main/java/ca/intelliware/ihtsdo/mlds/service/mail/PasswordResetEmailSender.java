package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.User;

import com.google.common.collect.Maps;

@Service
public class PasswordResetEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;
	
	public void sendPasswordResetEmail(User user, String tokenKey) {
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.USER, user);
		variables.put(EmailVariables.PASSWORD_RESET_URL, clientLinkBuilder.buildPasswordResetLink(tokenKey));
		String content = templateEvaluator.evaluateTemplate("passwordResetEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("passwordReset", locale);
		
		mailService.sendEmail(user.getEmail(), subject, content, false, true);
	}
}