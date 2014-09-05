package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

/**
 * AuthenticationProvider implementation that queries the IHTSDO Stormpath wrapper.
 */
//@Service
public class HttpAuthAuthenticationProvider implements AuthenticationProvider{
	@Resource
	HttpAuthAdaptor httpAuthAdaptor;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePassword = (UsernamePasswordAuthenticationToken) authentication;
			
			String username = usernamePassword.getName();
			String password = (String) usernamePassword.getCredentials();
			
			try {
				//httpAuthAdaptor.
				boolean isValid = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password);
				
				if (isValid) {
					return new UsernamePasswordAuthenticationToken(username, password,Arrays.asList(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)));
				} else {
					throw new UsernameNotFoundException("");
				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to contact authentication system.", e);
			}
			
		} else {
			throw new IllegalArgumentException("Unsupported type of authentication request: " + authentication.getClass());
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
