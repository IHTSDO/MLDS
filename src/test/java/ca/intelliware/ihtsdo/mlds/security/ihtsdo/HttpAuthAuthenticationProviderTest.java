package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RunWith(MockitoJUnitRunner.class)
public class HttpAuthAuthenticationProviderTest {
	HttpAuthAuthenticationProvider authenticationProvider = new HttpAuthAuthenticationProvider();
	
	@Mock
	HttpAuthAdaptor httpAuthAdaptorMock;
	
	@Before
	public void setUp() {
		authenticationProvider.httpAuthAdaptor = httpAuthAdaptorMock;
	}

	@Test
	public void providerSupportsUsernamePassword() throws Exception {
		assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
	}
	
	@Test
	public void successReturnsAnAuthentication() throws Exception {
		Mockito.stub(httpAuthAdaptorMock.checkUsernameAndPasswordValid("username", "password")).toReturn(true);
		
		Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
		
		assertNotNull(authentication);
	}
	
	@Test(expected=AuthenticationException.class)
	public void failureThrowsAnAuthException() throws Exception {
		Mockito.stub(httpAuthAdaptorMock.checkUsernameAndPasswordValid("username", "badpassword")).toReturn(false);
		
		Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("username", "password"));
	}
}
