package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

/**
 * Provide access check helpers for our rest controllers.
 * FIXME MLDS-23
 */
abstract class AuthorizationChecker {

	@Resource
	AffiliateRepository affiliateRepository;
	
	@Resource
	protected
	CurrentSecurityContext currentSecurityContext;
	
	protected boolean isStaffOrAdmin() {
		return currentSecurityContext.isStaffOrAdmin();
	}
	
	// FIXME MLDS-256 MB inline this?
	public String getCurrentUserName() {
		return currentSecurityContext.getCurrentUserName();
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
			//FIXME which exception should actually be used? Something that turns into an appropriate HTTP security response code
			failCheck("User not authorized to access Affiliate");
		}
	}

	public void checkCanAccessAffiliate(String username) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsUser(username);
	}


	public void checkCanAccessAffiliate(long affiliateId) {
		if (isStaffOrAdmin()) {
			return;
		}
		Affiliate Affiliate = affiliateRepository.findOne(affiliateId);
		checkCurrentUserIsMemberOfAffiliate(Affiliate);
	}

}
