package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Member;

/*
 * Send email notification to the relevant Member staff when an affiliate has applied in their space.
 * 
 * Use a shared email mailing list rather than to individual MLDS users as staff/admin are not registered
 * directly in the MLDS system, so we instead use an IHTSDO hosted mailing list email address for the
 * member organization.
 * 
 * https://jira.ihtsdotools.org/browse/MLDS-918
 */

@Service
public class ApplicationPendingEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;

	public void sendApplicationPendingEmail(String mailingListEmail, Application application) {
		final Locale locale = Locale.ENGLISH;
		Member member = application.getMember();
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.APPLICATION_ID, Long.toString(application.getApplicationId()));
		variables.put(EmailVariables.APPLICATION_MEMBER, memberDescription(member));
		variables.put(EmailVariables.VIEW_APPLICATION_URL, clientLinkBuilder.buildViewApplication(application.getApplicationId()));
		String content = templateEvaluator.evaluateTemplate("applicationPendingEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("applicationPending", locale);
		
		mailService.sendEmail(mailingListEmail, subject, content, false, true);
	}

	private String memberDescription(Member member) {
		return Strings.isNullOrEmpty(member.getName()) ? member.getKey() : member.getName();
	}

}
