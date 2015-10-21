package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.User;

/*
 * Send email announcement to the relevant Member affiliates.
 * 
 * https://jira.ihtsdotools.org/browse/MLDS-860
 */

@Service
public class AnnouncementEmailSender {
	@Resource MailService mailService;
	@Resource TemplateEvaluator templateEvaluator;
	@Resource ClientLinkBuilder clientLinkBuilder;

	public void sendAnnouncementEmail(User user, Member member, String title, String message) {
		String fullName = user.getFirstName() + ' ' + user.getLastName();
		final Locale locale = Locale.forLanguageTag(user.getLangKey());
		sendAnnouncementEmail(user.getEmail(), fullName, locale, member, title, message);
	}

	public void sendAnnouncementEmail(String email, Member member, String title, String message) {
		sendAnnouncementEmail(email, "", Locale.ENGLISH, member, title, message);
	}

	public void sendAnnouncementEmail(String email, String fullName, Locale locale, Member member, String title, String message) {
		String subject = title; 
		if (StringUtils.isBlank(subject)) {
			subject = templateEvaluator.getTitleFor("announcement", locale);
		}
		
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.ANNOUNCEMENT_NAME, fullName);
		variables.put(EmailVariables.ANNOUNCEMENT_MESSAGE, message);
		variables.put(EmailVariables.ANNOUNCEMENT_TITLE, subject);
		variables.put(EmailVariables.LOGIN_URL, clientLinkBuilder.buildLoginLink());
		
		String content = templateEvaluator.evaluateTemplate("announcementEmail", locale, variables);
		
		mailService.sendEmail(email, subject, content, false, true);
	}
}
