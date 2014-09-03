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

	public void sendApplicationApprovalEmail(User user, String memberKey, long affiliateId) {
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.AFFILIATE_ID, affiliateId);
		variables.put(EmailVariables.USER, user);
		variables.put(EmailVariables.MEMBERKEY, memberKey);
		variables.put(EmailVariables.LOGIN_URL, clientLinkBuilder.buildLoginLink());
		variables.put(EmailVariables.VIEW_PACKAGES_URL, clientLinkBuilder.buildViewPackagesLink());
		String content = templateEvaluator.evaluateTemplate("applicationApprovedEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("applicationApproved", locale);
		
		mailService.sendEmail(user.getEmail(), subject, content, false, true);
	}

}
