package ca.intelliware.ihtsdo.mlds.service.mail;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import ca.intelliware.ihtsdo.mlds.domain.Member;

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

	public void sendAnnouncementEmail(String email, Member member, String subject, String message) {
		Map<String, Object> variables = Maps.newHashMap();
		variables.put(EmailVariables.BLANK_TITLE, subject);
		variables.put(EmailVariables.BLANK_BODY, message);
		
		String content = templateEvaluator.evaluateTemplate("blankEmail", Locale.ENGLISH, variables);
		
		mailService.sendEmail(email, subject, content, false, true);
	}
}
