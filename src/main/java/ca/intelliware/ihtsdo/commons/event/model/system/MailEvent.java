package ca.intelliware.ihtsdo.commons.event.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.AccessType;

@Entity
public abstract class MailEvent extends SystemEvent {

	@Column(nullable=false,length=255)
	private String toEmailAddress;
	@Column(length=1024)
	private String subject;
	@Column(length=255, name="event_sub_type")
	private String type;

	protected MailEvent() {
	}
	public MailEvent(String description, String toEmailAddress, String subject, String type) {
		super(description);
		this.toEmailAddress = toEmailAddress;
		this.subject = subject;
		this.type = type;
	}
	
	public String getToEmailAddress() {
		return this.toEmailAddress;
	}
	public String getSubject() {
		return this.subject;
	}
	public String getType() {
		return this.type;
	}
}
