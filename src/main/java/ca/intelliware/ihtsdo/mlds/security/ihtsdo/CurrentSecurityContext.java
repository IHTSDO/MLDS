package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;



@Service
public class CurrentSecurityContext {

	public String getCurrentUserName() {
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		} else {
			return "MISSING PRINCIPAL";
		}
	}

	private Authentication getAuthentication() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		return authentication;
	}

	public boolean isAdmin() {
		return hasRole(AuthoritiesConstants.ADMIN);
	}

	private boolean hasRole(String role) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (securityContext.getAuthentication() != null) {
			for (GrantedAuthority authority : securityContext.getAuthentication().getAuthorities()) {
				if (role.equals(authority.getAuthority())) {
					return true;
				}
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

	public boolean isMemberFor(Member member) {
		Validate.notNull(member);
		return hasRole(AuthoritiesConstants.memberRoleForMember(member.getKey()));
	}

	public boolean isMember() {
		return hasRole(AuthoritiesConstants.MEMBER);
	}

	public boolean isMemberOrStaffOrAdmin() {
		return isMember() || isStaffOrAdmin();
	}

	public String getStaffMemberKey() {
		if (isAdmin()) {
			return Member.KEY_IHTSDO;
		} else if (isStaff() || isMember()) {
			for (GrantedAuthority authority : getAuthentication().getAuthorities()) {
				String a = authority.getAuthority();
				if (a.startsWith(AuthoritiesConstants.STAFF) &&
					!a.equals(AuthoritiesConstants.STAFF)) {
					return a.substring(AuthoritiesConstants.STAFF.length()+1); // +1 for underscore
				} else if (a.startsWith(AuthoritiesConstants.MEMBER) &&
						!a.equals(AuthoritiesConstants.MEMBER)) {
						return a.substring(AuthoritiesConstants.MEMBER.length()+1); // +1 for underscore
					}
			}
		}
		throw new IllegalStateException("User does not represent member");
	}

	public RemoteUserDetails getRemoteUserDetails() {
		RemoteUserDetails remoteUserDetails =  (RemoteUserDetails) getAuthentication().getPrincipal();
		return remoteUserDetails;
	}

	public List<String> getRolesList() {
		if (!getAuthentication().isAuthenticated()) {
			return Arrays.asList();
		} else {
			ArrayList<String> result = Lists.newArrayList();
			for (GrantedAuthority grantedAuthority : getAuthentication().getAuthorities()) {
				result.add(grantedAuthority.getAuthority());
			}
			return result;
		}
	}

	public void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

}