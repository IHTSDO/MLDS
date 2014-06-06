package ca.intelliware.ihtsdo.commons.event.model.user;

import javax.persistence.Entity;

@Entity
public class LogoutEvent extends UserEvent {
	
	protected LogoutEvent() {
		super();
	}
	
	public LogoutEvent(String principal, String ipAddress, String sessionId, String browserType, String browserVersion) {
		super("User logged out", principal, ipAddress, sessionId, browserType, browserVersion);
	}

}
