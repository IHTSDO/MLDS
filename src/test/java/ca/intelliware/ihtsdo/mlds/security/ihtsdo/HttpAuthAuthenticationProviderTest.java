package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class HttpAuthAuthenticationProviderTest {
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
		stubUserPasswordCheckResult("username", "password", true);
		
		Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
		
		assertNotNull(authentication);
	}
	
	@Test()
	public void badPasswordThrowsAnAuthException() throws Exception {
		stubUserLookup("username", remoteUserInfo);
		stubUserPasswordCheckResult("username", "badpassword", false);
		
		try {
			authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "badpassword"));
		} catch (BadCredentialsException e) {
			// expected
		}
	}

	@Test()
	public void nouserThrowsUsernameNotFoundException() throws Exception {
		stubUserLookup("username", null);
		stubUserPasswordCheckResult("username", "badpassword", false);
		
		try {
			authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "badpassword"));
		} catch (UsernameNotFoundException e) {
			// expected
		}
	}
	
	@Test
	public void successfulAuthenticationHasAuthorities() throws Exception {
		stubUserLookup("username", remoteUserInfo);
		stubUserPasswordCheckResult("username", "password", true);
		Mockito.stub(httpAuthAdaptorMock.getUserPermissions("username")).toReturn(Arrays.asList(adminUserPermission));
		
		Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
		
		assertThat(authentication.getAuthorities(), not(empty()));
	}


	private void stubUserLookup(String username, CentralAuthUserInfo centralAuthUserInfo) throws IOException {
		Mockito.stub(httpAuthAdaptorMock.getUserInfo(username)).toReturn(centralAuthUserInfo);
	}

	private void stubUserPasswordCheckResult(String username, String password, boolean value) throws IOException, ClientProtocolException {
		Mockito.stub(httpAuthAdaptorMock.checkUsernameAndPasswordValid(username, password)).toReturn(value);
	}
}
