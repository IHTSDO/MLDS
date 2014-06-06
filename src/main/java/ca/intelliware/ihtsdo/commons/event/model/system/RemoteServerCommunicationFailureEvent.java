package ca.intelliware.ihtsdo.commons.event.model.system;

import javax.persistence.Entity;

import org.hibernate.annotations.AccessType;

@Entity
@AccessType("field")
public class RemoteServerCommunicationFailureEvent extends SystemEvent {

	private String serverName;

	protected RemoteServerCommunicationFailureEvent() {
		super();
	}

	public RemoteServerCommunicationFailureEvent(String description, String serverName) {
		super(description);
		this.serverName = serverName;
	}

	public String getServerName() {
		return this.serverName;
	}
}
