package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.User;

import com.google.common.collect.Maps;

@Service
public class DuplicateRegistrationEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;
	
	public void sendDuplicateRegistrationEmail(User user, String resetTokenKey) {
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.USER, user);
		variables.put(EmailVariables.PASSWORD_RESET_URL, clientLinkBuilder.buildPasswordResetLink(resetTokenKey));
		variables.put(EmailVariables.LOGIN_URL, clientLinkBuilder.buildLoginLink());
		String content = templateEvaluator.evaluateTemplate("duplicateRegistrationEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("duplicateRegistration", locale);
		
		mailService.sendEmail(user.getEmail(), subject, content, false, true);
	}

}
