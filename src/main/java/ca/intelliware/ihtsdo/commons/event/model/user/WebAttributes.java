package ca.intelliware.ihtsdo.commons.event.model.user;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;

import ca.intelliware.commons.j5goodies.http.Browser;
import ca.intelliware.commons.j5goodies.http.HttpUtil;

/**
 * <p>A class that keeps track of some of the common attributes of a web request.
 * Such attributes include the sender's IP address (or the IP address of the nearest
 * proxy), the session id, details about the browser and locale.
 * 
 * @author <a href="http://www.intelliware.ca/">Intelliware Development</a>
 */
@Embeddable
public class WebAttributes {
	@Column(length=32)
	private String ipAddress;
	@Column(length=255)
	private String sessionId;
	@Column(length=32)
	private String browserType;
	@Column(length=32)
	private String browserVersion;
	@Column(length=1024)
	private String userAgent;
	@Column(length=5)
	private String locale;
	
	protected WebAttributes() {
	}
	public WebAttributes(String ipAddress, String sessionId, String browserType, String browserVersion) {
		this.ipAddress = ipAddress;
		this.sessionId = sessionId;
		this.browserType = browserType;
		this.browserVersion = browserVersion;
	}
	public WebAttributes(String ipAddress, String sessionId, String browserType, String browserVersion, String userAgent, Locale locale) {
		this.ipAddress = ipAddress;
		this.sessionId = sessionId;
		this.browserType = browserType;
		this.browserVersion = browserVersion;
		this.userAgent = userAgent;
		this.locale = StringUtils.left((locale == null) ? null : locale.toString(), 5);
	}
	public String getIpAddress() {
		return this.ipAddress;
	}
	public String getSessionId() {
		return this.sessionId;
	}
	public String getBrowserType() {
		return this.browserType;
	}
	public String getBrowserVersion() {
		return this.browserVersion;
	}
	public static WebAttributes create(HttpServletRequest httpRequest) {
		Browser browser = Browser.parse(httpRequest);
		return new WebAttributes(httpRequest.getRemoteAddr(), 
				httpRequest.getSession(true).getId(), 
				browser.getType().toString(), browser.getVersion(), 
				HttpUtil.getUserAgent(httpRequest),
				httpRequest.getLocale());
	}
	public String getUserAgent() {
		return this.userAgent;
	}
	public Locale getLocale() {
		return LocaleUtils.toLocale(this.locale);
	}
}
