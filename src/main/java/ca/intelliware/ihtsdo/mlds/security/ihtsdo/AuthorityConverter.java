package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

import com.google.common.collect.Lists;

/**
 * Converts permissions from the remote system into simple role Strings for local consumption.
 * @see CentralAuthUserPermission
 * @see AuthoritiesConstants
 */
public class AuthorityConverter {

	static final String REMOTE_MEMBER_IHTSDO = "INTL";
	static final String REMOTE_ROLE_ADMIN = "Admin";
	static final String REMOTE_ROLE_STAFF = "Staff";

	List<GrantedAuthority> buildAuthoritiesList(List<CentralAuthUserPermission> userPermissions) {
		List<GrantedAuthority> authorities = Lists.newArrayList();
		for (CentralAuthUserPermission centralAuthUserPermission : userPermissions) {
			if (REMOTE_ROLE_ADMIN.equals(centralAuthUserPermission.getRole())) {
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
			} else if (REMOTE_ROLE_STAFF.equals(centralAuthUserPermission.getRole())) {
				String memberKey = getMemberKey(centralAuthUserPermission);
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.STAFF));
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.staffRoleForMember(memberKey)));
			}
		}
		return authorities;
	}

	private String getMemberKey(CentralAuthUserPermission centralAuthUserPermission) {
		String remoteMemberKey = centralAuthUserPermission.getMember();
		if (Arrays.asList(REMOTE_MEMBER_IHTSDO, AuthoritiesConstants.IHTSDO).contains(remoteMemberKey)) {
			return AuthoritiesConstants.IHTSDO;
		}
		return remoteMemberKey;
	}
	
}