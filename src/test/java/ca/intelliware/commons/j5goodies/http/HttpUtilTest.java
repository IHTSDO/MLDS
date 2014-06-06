package ca.intelliware.commons.j5goodies.http;

import java.net.URL;

import junit.framework.TestCase;
import ca.intelliware.mockj2ee.servlet.MockHttpServletRequest;

public class HttpUtilTest extends TestCase {

    public void testNormalize() throws Exception {
        assertEquals("double slash", "/space/Project/Sample", HttpUtil.normalize("/space//Project/Sample"));
        assertEquals("..", "/space/Project/Sample", HttpUtil.normalize("/space/iProving/../Project/Sample"));
        assertEquals(".", "/space/Project/Sample", HttpUtil.normalize("/space/./Project/Sample"));
        assertEquals("space", "/space/Project/Sample Project", HttpUtil.normalize("/space/Project/Sample Project"));
    }
    
    public void testUrlWithoutPath() throws Exception {
        assertEquals("host name", "http://localhost:8080/", HttpUtil.getUrlWithoutPath(
                "http://localhost:8080/something/"));
    }
    
    public void testUrlWithInsaneDoubleSlash() throws Exception {
    	MockHttpServletRequest request = new MockHttpServletRequest(new URL("http://localhost:8080//prototype/myResource.txt"), "/prototype");
    	assertEquals("url", "/myResource.txt", HttpUtil.getRequestURIWithoutContext(request));
    }
    public void testUrlWithInsaneDoubleSlashAndNoResource() throws Exception {
    	MockHttpServletRequest request = new MockHttpServletRequest(new URL("http://localhost:8080//prototype"), "/prototype");
    	assertEquals("url", "", HttpUtil.getRequestURIWithoutContext(request));
    }
    
    public void testUrlWithoutPath2() throws Exception {
        assertEquals("host name", "http://www.cbc.ca/", HttpUtil.getUrlWithoutPath(
                "http://www.cbc.ca/weather/index.html"));
    }
    
    public void testHostNameLocalhost() throws Exception {
        assertEquals("host name", "localhost", HttpUtil.getHighLevelDomainName(
                "http://localhost:8080/something/"));
    }
    
    public void testHostNameCBC() throws Exception {
        assertEquals("host name", "cbc.ca", HttpUtil.getHighLevelDomainName(
                "http://www.cbc.ca/weather/"));
    }
    
    public void testHostNameSourceforge() throws Exception {
        assertEquals("host name", "sourceforge.net", HttpUtil.getHighLevelDomainName(
                "http://sourceforge.net/projects/quantum/"));
    }
    
    public void testHostNameBogus() throws Exception {
        assertNull("host name", HttpUtil.getHighLevelDomainName(
                "http://bogus/something/"));
    }
    
    public void testPrivateNetwork() throws Exception {
    	MockHttpServletRequest httpRequest = new MockHttpServletRequest();
    	assertPrivateNetwork("192.168.3.55", httpRequest);
    	assertPrivateNetwork("192.168.2.42", httpRequest);
    	assertPrivateNetwork("172.16.4.99", httpRequest);
    }

	private void assertPrivateNetwork(String ipAddress, MockHttpServletRequest httpRequest) {
    	httpRequest.setRemoteAddr(ipAddress);
		assertTrue(ipAddress, HttpUtil.isPrivateNetwork(httpRequest));
	}
    
	private void assertNotPrivateNetwork(String ipAddress, MockHttpServletRequest httpRequest) {
		httpRequest.setRemoteAddr(ipAddress);
		assertFalse(ipAddress, HttpUtil.isPrivateNetwork(httpRequest));
	}
	
    public void testNotPrivateNetwork() throws Exception {
    	MockHttpServletRequest httpRequest = new MockHttpServletRequest();
    	assertNotPrivateNetwork("172.168.3.55", httpRequest);
    	assertNotPrivateNetwork("254.254.2.19", httpRequest);
    	assertNotPrivateNetwork("12.8.16.172", httpRequest);
    	assertNotPrivateNetwork("not a valid ip address", httpRequest);
    }
    
    public void testQualifyUrlWithEmptyContextPath() throws Exception {
    	assertEquals("path", "/some/path", HttpUtil.qualifyUrlWithContextPath("/some/path", ""));
	}
    public void testQualifyUrlWithNotEmptyContextPath() throws Exception {
    	assertEquals("path", "/myApp/some/path", HttpUtil.qualifyUrlWithContextPath("/some/path", "/myApp"));
    }
    public void testQualifyUrlWithNotEmptyContextPathHttpRequest() throws Exception {
    	assertEquals("path", "/myApp/some/path", HttpUtil.qualifyUrlWithContextPath("/some/path", new MockHttpServletRequest("http://localhost:8080/myApp/index.html")));
    }
    public void testQualifyUrlWithEmptyContextPathHttpRequest() throws Exception {
    	assertEquals("path", "/some/path", HttpUtil.qualifyUrlWithContextPath("/some/path", 
    			new MockHttpServletRequest(new URL("http://localhost:8080/notpartofthecontext/index.html"), "")));
    }
}
