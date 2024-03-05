package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Adapt the CentralAuthUserInfo to the Spring UserDetails interface.
 */
public class RemoteUserDetails implements UserDetails {
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
		return centralAuthUserInfo.getLogin();
	}


	public CentralAuthUserInfo getCentralAuthUserInfo() {
		return centralAuthUserInfo;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}