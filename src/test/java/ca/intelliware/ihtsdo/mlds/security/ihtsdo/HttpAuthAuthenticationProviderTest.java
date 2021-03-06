package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

/*import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
*/

//@RunWith(MockitoJUnitRunner.class)
public class HttpAuthAuthenticationProviderTest {
/*	
	HttpAuthAuthenticationProvider authenticationProvider = new HttpAuthAuthenticationProvider();
	
	@Mock
	HttpAuthAdaptor httpAuthAdaptorMock;
	
	CentralAuthUserPermission adminUserPermission = new CentralAuthUserPermission();
	CentralAuthUserInfo remoteUserInfo = new CentralAuthUserInfo();

	@Before
	public void setUp() {
		authenticationProvider.httpAuthAdaptor = httpAuthAdaptorMock;
		
		adminUserPermission.setApp("MLDS");
		adminUserPermission.setMember("INTL");
		adminUserPermission.setRole("Admin");
	}

	@Test
	public void providerSupportsUsernamePassword() throws Exception {
		assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
	}
	
	@Test
	public void successReturnsAnAuthentication() throws Exception {
		stubUserLookup("username", remoteUserInfo);
		stubUserPasswordCheckResult("username", "password", null);
		//stubUserPerms("username", adminUserPermission);

		Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
		
		assertNotNull(authentication);
	}
	
	@Test()
	public void badPasswordThrowsAnAuthException() throws Exception {
		stubUserLookup("username", remoteUserInfo);
		stubUserPasswordCheckResult("username", "badpassword", null);
		
		try {
			authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "badpassword"));
		} catch (BadCredentialsException e) {
			// expected
		}
	}

	@Test()
	public void nouserThrowsUsernameNotFoundException() throws Exception {
		stubUserLookup("username", null);
		stubUserPasswordCheckResult("username", "badpassword", null);
		
		try {
			authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "badpassword"));
		} catch (UsernameNotFoundException e) {
			// expected
		}
	}
	
	@Test
	public void successfulAuthenticationHasAuthorities() throws Exception {
		stubUserLookup("username", remoteUserInfo);
		stubUserPasswordCheckResult("username", "password", null);
		//stubUserPerms("username", adminUserPermission);
		
		Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
		
		assertThat(authentication.getAuthorities(), not(empty()));
	}



	private void stubUserLookup(String username, CentralAuthUserInfo centralAuthUserInfo) throws IOException {
		Mockito.stub(httpAuthAdaptorMock.getUserAccountInfo(username, null)).toReturn(centralAuthUserInfo);
	}

	private void stubUserPasswordCheckResult(String username, String password, String value) throws IOException {
		Mockito.stub(httpAuthAdaptorMock.checkUsernameAndPasswordValid(username, password)).toReturn(value);
	}
	*/
}
