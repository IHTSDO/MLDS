package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.User;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

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
		sendAnnouncementEmail(user.getEmail(), member, title, message);
	}
	
	public void sendAnnouncementEmail(String email, Member member, String title, String message) {
		final Locale locale = Locale.ENGLISH;
		Map<String, Object> variables = Maps.newHashMap();
//		variables.put(EmailVariables.APPLICATION_ID, Long.toString(application.getApplicationId()));
//		variables.put(EmailVariables.APPLICATION_MEMBER, memberDescription(member));
//		variables.put(EmailVariables.VIEW_APPLICATION_URL, clientLinkBuilder.buildViewApplication(application.getApplicationId()));
		String content = templateEvaluator.evaluateTemplate("announcementEmail", locale, variables);
		String subject = templateEvaluator.getTitleFor("announcement", locale);
		
		mailService.sendEmail(email, subject, content, false, true);
	}

	private String memberDescription(Member member) {
		return Strings.isNullOrEmpty(member.getName()) ? member.getKey() : member.getName();
	}

}
