package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;

import com.google.common.base.Objects;

/**
 * Provide access check helpers for our rest controllers.
 */
public class AuthorizationChecker {

	@Resource
	protected CurrentSecurityContext currentSecurityContext;
	
	protected boolean isStaffOrAdmin() {
		return currentSecurityContext.isStaffOrAdmin();
	}
	
	protected boolean isAdmin() {
		return currentSecurityContext.isAdmin();
	}
	
	protected boolean isUser() {
		return currentSecurityContext.isUser();
	}
	
	protected void failCheck(String description) {
		//FIXME which exception should actually be used? Something that turns into an appropriate HTTP security response code
		throw new IllegalStateException(description);
	}
	
	protected void checkCurrentUserIsMemberOfAffiliate(Affiliate affiliate) {
		if (affiliate != null) {
			checkCurrentUserIsUser(affiliate.getCreator());
		}
	}

	protected void checkCurrentUserIsUser(String username) {
		if (! ObjectUtils.equals(currentSecurityContext.getCurrentUserName(), username)) {
			failCheck("User not authorized to access Affiliate");
		}
	}

	public void checkCanAccessAffiliate(String username) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsUser(username);
	}


	public void checkCanAccessAffiliate(Affiliate affiliate) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsMemberOfAffiliate(affiliate);
	}

	protected boolean isAdminOrStaffOfMember(String memberKey) {
		if (currentSecurityContext.isAdmin()) {
			return true;
		}
		if (currentSecurityContext.isStaff()
				&& Objects.equal(currentSecurityContext.getStaffMemberKey(), memberKey)) {
			return true;
		}
		return false;

	}
	
	public void checkCanManageAffiliate(Affiliate affiliate) {
		if (isAdminOrStaffOfMember(affiliate.getHomeMemberKey())) {
			return;
		}
		failCheck("User not authorized to manage Affiliate");
	}
	
	public void setCurrentSecurityContext(CurrentSecurityContext currentSecurityContext) {
		this.currentSecurityContext = currentSecurityContext;
	}
}
