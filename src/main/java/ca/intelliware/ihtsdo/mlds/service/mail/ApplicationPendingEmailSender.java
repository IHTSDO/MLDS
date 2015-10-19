package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.User;

import com.google.common.collect.Maps;

/*
 * Send out email notification to the relevant Member staff when an affiliate has applied in their space.
 * 
 * https://jira.ihtsdotools.org/browse/MLDS-918
 */

@Service
public class ApplicationPendingEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;

	public void sendApplicationPendingEmail(String email, String memberKey, long affiliateId, long applicationId) {
		//FIXME shared account - which locale?
		final Locale locale = Locale.forLanguageTag("en");
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.AFFILIATE_ID, Long.toString(affiliateId));
		variables.put(EmailVariables.APPLICATION_ID, Long.toString(applicationId));
		variables.put(EmailVariables.USER, email);
		variables.put(EmailVariables.MEMBERKEY, memberKey);
		variables.put(EmailVariables.VIEW_APPLICATION_URL, clientLinkBuilder.buildViewApplication(applicationId));
		String content = templateEvaluator.evaluateTemplate("applicationApprovedEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("applicationApproved", locale);
		
		mailService.sendEmail(email, subject, content, false, true);
	}

}
