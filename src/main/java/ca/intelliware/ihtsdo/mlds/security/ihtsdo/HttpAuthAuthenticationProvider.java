package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * AuthenticationProvider implementation that queries the IHTSDO Stormpath wrapper.
 */
@Service
public class HttpAuthAuthenticationProvider implements AuthenticationProvider{
	@Resource
	HttpAuthAdaptor httpAuthAdaptor;

	AuthorityConverter authorityConverter = new AuthorityConverter(); 

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePassword = (UsernamePasswordAuthenticationToken) authentication;
			
			String username = usernamePassword.getName();
			String password = (String) usernamePassword.getCredentials();
			
			try {
				CentralAuthUserInfo remoteUserInfo = httpAuthAdaptor.getUserInfo(username);
				if (remoteUserInfo == null) {
					throw new UsernameNotFoundException("User not found in remote system: " + username);
				}
				
				boolean isValid = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password);
				if (!isValid) {
					throw new BadCredentialsException("MLDS_ERR_AUTH_BAD_PASSWORD: Password for remote user was invalid: " + username);
				}
				
				List<CentralAuthUserPermission> userPermissions = httpAuthAdaptor.getUserPermissions(username);
				List<GrantedAuthority> authorities = authorityConverter.buildAuthoritiesList(userPermissions);
				if (authorities.isEmpty()) {
					throw new DisabledException("MLDS_ERR_AUTH_NO_PERMISSIONS: Users exists, but has no permissions assigned: "+ username);
				}
				
				RemoteUserDetails user = new RemoteUserDetails(remoteUserInfo, authorities);
				return new UsernamePasswordAuthenticationToken(user, password,authorities);
				
			} catch (IOException e) {
				throw new RuntimeException("MLDS_ERR_AUTH_SYSTEM: Failed to contact authentication system.", e);
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
