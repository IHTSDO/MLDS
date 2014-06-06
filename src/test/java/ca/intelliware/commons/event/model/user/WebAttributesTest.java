package ca.intelliware.commons.event.model.user;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import ca.intelliware.ihtsdo.commons.event.model.user.LoginEvent;
import ca.intelliware.ihtsdo.commons.event.model.user.WebAttributes;
import ca.intelliware.mockj2ee.servlet.MockHttpServletRequest;


public class WebAttributesTest {
	
	@Test
	public void shouldPopulateWebAttributesFromHttpRequest() throws Exception {
		
		MockHttpServletRequest request = createHttpRequest();
		WebAttributes attributes = WebAttributes.create(request);
		
		assertEquals("locale", Locale.GERMAN, attributes.getLocale());
		assertEquals("session id", "mySessionId", attributes.getSessionId());
		assertEquals("Browser type", "Firefox", attributes.getBrowserType());
		assertEquals("Browser version", "3.0.5", attributes.getBrowserVersion());
		assertEquals("IP address", "192.168.3.222", attributes.getIpAddress());
	}

	@Test
	public void shouldProvideDelegateMethodsInEvent() throws Exception {
		
		MockHttpServletRequest request = createHttpRequest();
		LoginEvent event = new LoginEvent(null, WebAttributes.create(request));
		
		assertEquals("locale", Locale.GERMAN, event.getLocale());
		assertEquals("session id", "mySessionId", event.getSessionId());
		assertEquals("Browser type", "Firefox", event.getBrowserType());
		assertEquals("Browser version", "3.0.5", event.getBrowserVersion());
		assertEquals("IP address", "192.168.3.222", event.getIpAddress());
		
		assertEquals("Browser", "Firefox 3.0.5", event.getBrowser());
	}
	
	private MockHttpServletRequest createHttpRequest() {
		MockHttpServletRequest.getSessionForId("mySessionId");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setHeader("user-agent", 
				"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.5) Gecko/2008121621 Ubuntu/8.04 (hardy) Firefox/3.0.5");
		request.setLocale(Locale.GERMAN);
		request.setRemoteAddr("192.168.3.222");
		request.setHeader(MockHttpServletRequest.SESSION_HEADER_KEY, "mySessionId");
		return request;
	}

}
