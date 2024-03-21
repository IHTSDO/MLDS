package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import ca.intelliware.ihtsdo.mlds.domain.ApplicationErrorCodes;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;

/**
 * AuthenticationProvider implementation that queries the IHTSDO Crowd wrapper.
 */
@Service
public class HttpAuthAuthenticationProvider implements AuthenticationProvider{
	@Autowired
	HttpAuthAdaptor httpAuthAdaptor;


	private final Logger logger = LoggerFactory.getLogger(HttpAuthAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username;
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePassword = (UsernamePasswordAuthenticationToken) authentication;

			username = usernamePassword.getName();
			String password = (String) usernamePassword.getCredentials();

			try {
				String authenticatedToken = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password);
//				String authenticatedToken=null;
				if (authenticatedToken == null) {
					throw new BadCredentialsException(ApplicationErrorCodes.MLDS_ERR_AUTH_BAD_PASSWORD
							+ ": Password for remote user was invalid: " + username);
				}
				CentralAuthUserInfo remoteUserInfo = httpAuthAdaptor.getUserAccountInfo(username, authenticatedToken);
				List<GrantedAuthority> authorities = AuthorityConverter.buildAuthoritiesList(remoteUserInfo.getRoles());

				if (authorities.isEmpty()) {
					throw new DisabledException(ApplicationErrorCodes.MLDS_ERR_AUTH_NO_PERMISSIONS
							+ ": Users exists, but has no permissions assigned: "+ username);
				}

				//If the user has logged in, also give them the USER role
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));

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
