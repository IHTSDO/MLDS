package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ca.intelliware.ihtsdo.mlds.domain.ApplicationErrorCodes;

/**
 * AuthenticationProvider implementation that queries the IHTSDO Crowd wrapper.
 */
@Service
public class HttpAuthAuthenticationProvider implements AuthenticationProvider{
	@Resource
	HttpAuthAdaptor httpAuthAdaptor;
	
	private final Logger logger = LoggerFactory.getLogger(HttpAuthAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = "unknown";
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePassword = (UsernamePasswordAuthenticationToken) authentication;
			
			username = usernamePassword.getName();
			String password = (String) usernamePassword.getCredentials();
			
			try {
				//First we need a cross scripting token before we can make any API calls
				String csrfToken = httpAuthAdaptor.getCsrfToken();
				if (!httpAuthAdaptor.checkUserExists(username, csrfToken)) {
					throw new UsernameNotFoundException("User not found in remote system: " + username);
				}
				
				HttpCookie authenticatedToken = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password, csrfToken);
				if (authenticatedToken == null) {
					throw new BadCredentialsException(ApplicationErrorCodes.MLDS_ERR_AUTH_BAD_PASSWORD
							+ ": Password for remote user was invalid: " + username);
				}
				CentralAuthUserInfo remoteUserInfo =  httpAuthAdaptor.getUserAccountInfo(username, csrfToken, authenticatedToken);
				List<GrantedAuthority> authorities = Lists.newArrayList();
				for (String role : remoteUserInfo.getRoles()) {
					if (role.contains("mlds")) {
						authorities.add(new SimpleGrantedAuthority(role));
					}
				}
				if (authorities.isEmpty()) {
					throw new DisabledException(ApplicationErrorCodes.MLDS_ERR_AUTH_NO_PERMISSIONS
							+ ": Users exists, but has no permissions assigned: "+ username);
				}
				
				RemoteUserDetails user = new RemoteUserDetails(remoteUserInfo, authorities);
				return new UsernamePasswordAuthenticationToken(user, password,authorities);
				
			} catch (IOException e) {
				throw new RuntimeException("MLDS_ERR_AUTH_SYSTEM: Failed to contact authentication system.", e);
			} catch (Exception e) {
				logger.info("Returning {} due to {}", e.getClass().getName(), e.getMessage());
				throw (e);
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
