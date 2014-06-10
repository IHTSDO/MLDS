package ca.intelliware.ihtsdo.commons.event.model.user;

import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Transient;

import ca.intelliware.ihtsdo.mlds.domain.Event;

import com.google.common.base.Strings;

@Entity
public abstract class UserEvent extends Event {

	private String principal;
	private WebAttributes attributes;

	protected UserEvent() {
	}
	public UserEvent(String description, String principal, String ipAddress, String sessionId, String browserType, String browserVersion) {
		super(description);
		this.principal = principal;
		this.attributes = new WebAttributes(ipAddress, sessionId, browserType, browserVersion);
	}
	public UserEvent(String description, String principal, WebAttributes attributes) {
		super(description);
		this.principal = principal;
		this.attributes = attributes;
	}
	
	public String getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getIpAddress() {
		return this.attributes == null ? null : this.attributes.getIpAddress();
	}
	public String getSessionId() {
		return this.attributes == null ? null : this.attributes.getSessionId();
	}
	public String getBrowserType() {
		return this.attributes == null ? null : this.attributes.getBrowserType();
	}
	public String getBrowserVersion() {
		return this.attributes == null ? null : this.attributes.getBrowserVersion();
	}
	public String getUserAgent() {
		return this.attributes == null ? null : this.attributes.getUserAgent();
	}
	public Locale getLocale() {
		return this.attributes == null ? null : this.attributes.getLocale();
	}
	@Transient
	public String getBrowser() {
		StringBuilder builder = new StringBuilder();
		if (!Strings.isNullOrEmpty(getBrowserType())) {
			builder.append(getBrowserType());
			builder.append(' ');
		}
		if (!Strings.isNullOrEmpty(getBrowserVersion())) {
			builder.append(getBrowserVersion());
		}
		return builder.toString().trim();
	}
}
