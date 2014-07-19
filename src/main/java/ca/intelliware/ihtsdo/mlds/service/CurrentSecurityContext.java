package ca.intelliware.ihtsdo.mlds.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

@Service
public class CurrentSecurityContext {

	public String getCurrentUserName() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return securityContext.getAuthentication().getName();
	}

	public boolean isAdmin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		for (GrantedAuthority authority : securityContext.getAuthentication().getAuthorities()) {
			if (AuthoritiesConstants.ADMIN.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		for (GrantedAuthority authority : securityContext.getAuthentication().getAuthorities()) {
			if (AuthoritiesConstants.USER.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
}