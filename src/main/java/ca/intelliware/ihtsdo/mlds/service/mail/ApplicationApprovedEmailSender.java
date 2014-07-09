package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.User;

import com.google.common.collect.Maps;

@Service
public class ApplicationApprovedEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;

	public void sendApplicationApprovalEmail(User user) {
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.USER, user);
		variables.put(EmailVariables.LOGIN_URL, clientLinkBuilder.buildLoginLink());
		String content = templateEvaluator.evaluateTemplate("applicationApprovedEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("applicationApproved", locale);
		
		mailService.sendEmail(user.getEmail(), subject, content, false, true);
	}

}
