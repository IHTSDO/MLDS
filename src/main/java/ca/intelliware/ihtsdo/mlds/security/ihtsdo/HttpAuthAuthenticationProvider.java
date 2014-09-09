package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CentralAuthUserInfo.Status;

import com.google.common.collect.Lists;

/**
 * AuthenticationProvider implementation that queries the IHTSDO Stormpath wrapper.
 */
@Service
public class HttpAuthAuthenticationProvider implements AuthenticationProvider{
	@Resource
	HttpAuthAdaptor httpAuthAdaptor;

	public static class RemoteUserDetails implements UserDetails {
		private static final long serialVersionUID = 1L;
		
		final CentralAuthUserInfo centralAuthUserInfo;
		final Collection<? extends GrantedAuthority> authorities;

		public RemoteUserDetails(CentralAuthUserInfo centralAuthUserInfo, Collection<? extends GrantedAuthority> authorities) {
			super();
			this.centralAuthUserInfo = centralAuthUserInfo;
			this.authorities = authorities;
		}
		
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return authorities;
		}

		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public String getUsername() {
			return centralAuthUserInfo.getName();
		}

		@Override
		public boolean isAccountNonExpired() {
			return centralAuthUserInfo.status == Status.ENABLED;
		}

		@Override
		public boolean isAccountNonLocked() {
			return centralAuthUserInfo.status == Status.ENABLED;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return centralAuthUserInfo.status == Status.ENABLED;
		}

		@Override
		public boolean isEnabled() {
			return centralAuthUserInfo.status == Status.ENABLED;
		}

		public CentralAuthUserInfo getCentralAuthUserInfo() {
			return centralAuthUserInfo;
		}
		
	}
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePassword = (UsernamePasswordAuthenticationToken) authentication;
			
			String username = usernamePassword.getName();
			String password = (String) usernamePassword.getCredentials();
			
			try {
				boolean isValid = httpAuthAdaptor.checkUsernameAndPasswordValid(username, password);
				CentralAuthUserInfo remoteUserInfo = httpAuthAdaptor.getUserInfo(username);
				
				if (isValid) {
			    	// FIXME MLDS-170 MB build permissions
					List<CentralAuthUserPermission> userPermissions = httpAuthAdaptor.getUserPermissions(username);
					List<SimpleGrantedAuthority> authorities = buildAuthoritiesList(userPermissions);
					
					RemoteUserDetails user = new RemoteUserDetails(remoteUserInfo, authorities);
					return new UsernamePasswordAuthenticationToken(user, password,authorities);
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

	private List<SimpleGrantedAuthority> buildAuthoritiesList(List<CentralAuthUserPermission> userPermissions) {
		List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
		for (CentralAuthUserPermission centralAuthUserPermission : userPermissions) {
			authorities.add(buildAuthorityFromRemoteUserPermission(centralAuthUserPermission));
		}
		// For debug for now.
		authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
		return authorities;
	}

	private SimpleGrantedAuthority buildAuthorityFromRemoteUserPermission(CentralAuthUserPermission centralAuthUserPermission) {
		// FIXME MLDS-170 MB staff vs admin, and add member param for staff role.
		return new SimpleGrantedAuthority(centralAuthUserPermission.getRole());
	}

	@Override
	public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
