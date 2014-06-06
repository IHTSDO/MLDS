package ca.intelliware.ihtsdo.commons.event.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class RegisterNewUserEvent extends UserEvent {
	
	@Enumerated(EnumType.STRING)
	@Column(name="event_sub_type", nullable=false)
	private RegistrationEventType type;	
	
	protected RegisterNewUserEvent() {
		super();
	}
	public RegisterNewUserEvent(RegistrationEventType type, String principal, WebAttributes webAttributes) {
		super(type.getDescription(), principal, webAttributes);
		this.type = type;
	}
	public RegistrationEventType getType() {
		return this.type;
	}
}
