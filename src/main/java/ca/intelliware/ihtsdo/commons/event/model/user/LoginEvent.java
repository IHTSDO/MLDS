package ca.intelliware.ihtsdo.commons.event.model.user;

import javax.persistence.Entity;

@Entity
public class LoginEvent extends UserEvent {

	private static final long serialVersionUID = 3541792787901870882L;

	protected LoginEvent() {
		super();
	}
	public LoginEvent(String principal, String ipAddress, String sessionId, String browserType, String browserVersion) {
		super("User logged in", principal, ipAddress, sessionId, browserType, browserVersion);
	}
	public LoginEvent(String principal, WebAttributes attributes) {
		super("User logged in", principal, attributes);
	}
}
