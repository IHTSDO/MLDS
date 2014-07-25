package ca.intelliware.ihtsdo.mlds.service;

import org.apache.commons.lang.Validate;
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
		Validate.notNull(member);
		return hasRole(AuthoritiesConstants.staffRoleForMember(member.getKey()));
	}

	public String getStaffMemberKey() {
		if (isAdmin()) {
			return Member.KEY_IHTSDO;
		} else if (isStaff()) {
			for (GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
				String a = authority.getAuthority();
				if (a.startsWith(AuthoritiesConstants.STAFF) &&
					!a.equals(AuthoritiesConstants.STAFF)) {
					return a.substring(AuthoritiesConstants.STAFF.length()+1); // +1 for underscore
				}
			}
		}
		throw new IllegalStateException("User does not represent member");
	}
	
}