package ca.intelliware.ihtsdo.commons.event.model.system;

import javax.persistence.Entity;

import org.hibernate.annotations.AccessType;

@Entity
@AccessType("field")
public class MailSendFailureEvent extends MailEvent {

	private String mailServerName;

	/**
	 * For JPA
	 */
	protected MailSendFailureEvent() {
		super();
	}
	
	public MailSendFailureEvent(String description, String toEmailAddress, String mailServerName) {
		super(description, toEmailAddress, null, null);
		this.mailServerName = mailServerName;
	}

	public String getMailServerName() {
		return this.mailServerName;
	}
}
