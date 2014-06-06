package ca.intelliware.ihtsdo.commons.event.model.system;

import javax.persistence.Entity;

import org.hibernate.annotations.AccessType;

import com.google.common.base.Strings;

/**
 * <p>This event indicates that the application has successfully sent an email message.
 * 
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
@Entity
public class MailSentEvent extends MailEvent {

	protected MailSentEvent() {
	}
	public MailSentEvent(String toEmailAddress, String subject) {
		this(null, toEmailAddress, subject, null);
	}
	public MailSentEvent(String description, String toEmailAddress, String subject, String type) {
		super(Strings.isNullOrEmpty(description) ? "Email sent" : "Email sent: " + description,
				toEmailAddress, subject, type);
	}
	
}
