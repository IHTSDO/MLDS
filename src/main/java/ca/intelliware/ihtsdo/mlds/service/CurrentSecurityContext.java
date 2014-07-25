package ca.intelliware.ihtsdo.mlds.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

@Service
public class CurrentSecurityContext {

	public String getCurrentUserName() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return securityContext.getAuthentication().getName();
	}

	public boolean isAdmin() {
		return hasRole(AuthoritiesConstants.ADMIN);
	}

	private boolean hasRole(String role) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		for (GrantedAuthority authority : securityContext.getAuthentication().getAuthorities()) {
			if (role.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isUser() {
		return hasRole(AuthoritiesConstants.USER);
	}

	public boolean isStaff() {
		return hasRole(AuthoritiesConstants.STAFF);
	}

	public boolean isStaffOrAdmin() {
		return isStaff() || isAdmin();
	}

	public boolean isStaffFor(Member member) {
		return hasRole(AuthoritiesConstants.staffRoleForMember(member.getKey()));
	}
	
}