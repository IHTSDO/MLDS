package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ca.intelliware.ihtsdo.mlds.security.AuthoritiesConstants;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Converts permissions from the remote system into simple role Strings for local consumption.
 * @see CentralAuthUserPermission
 * @see AuthoritiesConstants
 */
public class AuthorityConverter {

	static final String REMOTE_MEMBER_IHTSDO = "INTL";
	static final String REMOTE_MEMBER_STAR = "*";
	static final String REMOTE_ROLE_ADMIN = "ROLE_mlds-ihtsdo";
	static final String REMOTE_ROLE_STAFF = "ROLE_mlds-member-"; //Followed by the country code
	static final String REMOTE_ROLE_MEMBER = "ROLE_mlds-member";

	static List<GrantedAuthority> buildAuthoritiesList(List<String> userRoles) {
		List<GrantedAuthority> authorities = Lists.newArrayList();
		for (String role : userRoles) {
			if (REMOTE_ROLE_ADMIN.equals(role)) {
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.IHTSDO));
			} else if (role.startsWith(REMOTE_ROLE_STAFF)) {
				String memberKey = role.substring(REMOTE_ROLE_STAFF.length());
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.STAFF));
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.staffRoleForMember(memberKey)));
			} else if (REMOTE_ROLE_MEMBER.equals(role)) {
				//TODO Throw an exception until we've worked out how to recover the member country
				throw new NotImplementedException("TODO - Recover member's country");
				/*String memberKey = getMemberKey(centralAuthUserPermission);
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.MEMBER));
				if (StringUtils.isBlank(memberKey) || Objects.equal(memberKey, REMOTE_MEMBER_STAR)) {
					memberKey = AuthoritiesConstants.IHTSDO;
				}
				authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.memberRoleForMember(memberKey)));
				*/
			}
		}
		return authorities;
	}

	/*private String getMemberKey(CentralAuthUserPermission centralAuthUserPermission) {
		String remoteMemberKey = centralAuthUserPermission.getMember();
		if (Arrays.asList(REMOTE_MEMBER_IHTSDO, AuthoritiesConstants.IHTSDO).contains(remoteMemberKey)) {
			return AuthoritiesConstants.IHTSDO;
		}
		return remoteMemberKey;
	}*/
	
}