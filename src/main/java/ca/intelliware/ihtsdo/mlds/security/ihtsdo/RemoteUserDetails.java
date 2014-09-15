package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ca.intelliware.ihtsdo.mlds.security.ihtsdo.CentralAuthUserInfo.Status;

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