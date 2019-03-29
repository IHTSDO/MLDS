package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpCookie;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Ignore // Use explore behaviour
public class HttpAuthIntegrationTest {
	// https://usermanagement.ihtsdotools.org/security-web/query/users/AdamT/apps/Refset
	//https://usermanagement.ihtsdotools.org/security-web/query/?queryName=getUserAppPerms&username=AdamT&appName=Refset
	HttpAuthAdaptor httpAuthAdaptor = new HttpAuthAdaptor("https://usermanagement.ihtsdotools.org/security-web/query/");

	@Test
	public void loginWithGoodPasswordReturnsTrue() throws Exception {
		String username = "michaelb";
		String password = "XXXXXXXX";// put a real password in
		
		HttpCookie cookie = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password);
		
		assertNotNull(cookie);
	}
	
	@Test
	public void loginWithBadPasswordReturnsFalse() throws Exception {
		String username = "michaelb";
		String password = "nothepassword";
		
		HttpCookie cookie = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password);
		
		assertNull(cookie);
	}
	
	@Test(expected=IOException.class)
	public void badUrlInConfigThrowsExceptionRatherThanResult() throws Exception {
		httpAuthAdaptor = new HttpAuthAdaptor("https://usermanagement.ihtsdotools.org/notTheQueryUrl/");
		
		httpAuthAdaptor.checkUsernameAndPasswordValid("user", "password");
	}
}
