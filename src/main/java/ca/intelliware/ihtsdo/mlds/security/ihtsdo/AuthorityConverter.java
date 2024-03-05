package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;
import com.google.common.collect.Lists;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * Converts permissions from the remote system into simple role Strings for local consumption.
 * @see CentralAuthUserPermission
 * @see AuthoritiesConstants
 */
public class AuthorityConverter {

	static final String REMOTE_MEMBER_IHTSDO = "INTL";
	static final String REMOTE_MEMBER_STAR = "*";
	static final String REMOTE_ROLE_ADMIN = "ROLE_mlds-ihtsdo";
	static final String REMOTE_ROLE_STAFF = "ROLE_mlds-staff-"; //Followed by the country code
	static final String REMOTE_ROLE_MEMBER = "ROLE_mlds-member";

	static List<GrantedAuthority> buildAuthoritiesList(List<String> userRoles) {
		List<GrantedAuthority> authorities = Lists.newArrayList();
		for (String role : userRoles) {
			if (REMOTE_ROLE_ADMIN.equals(role)) {
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.IHTSDO));
			} else if (role.startsWith(REMOTE_ROLE_STAFF)) {
				String memberKey = role.substring(REMOTE_ROLE_STAFF.length()).toUpperCase();
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.STAFF));
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.staffRoleForMember(memberKey)));
			} else if (role.equals(REMOTE_ROLE_MEMBER)) {
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.MEMBER));
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.memberRoleForMember(AuthoritiesConstants.IHTSDO)));
			}
		}
		return authorities;
	}

}