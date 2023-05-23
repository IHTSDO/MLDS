package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;
import ca.intelliware.ihtsdo.mlds.service.UserMembershipAccessor;

@Service
public class ReleasePackageAuthorizationChecker extends AuthorizationChecker {

	@Resource UserMembershipAccessor userMembershipAccessor;
	@Resource UserStandingCalculator userStandingCalculator;

	public void checkCanCreateReleasePackages() {
		if (isStaffOrAdmin()) {
			return;
		}
		failCheck("User not authorized to access release packages.");
	}

	public void checkCanEditReleasePackage(ReleasePackage releasePackage) {
		if (currentSecurityContext.isAdmin()
				|| currentSecurityContext.isStaffFor(releasePackage.getMember())) {
			return;
		}
		failCheck("User not authorized to access release packages.");
	}

	boolean shouldSeeOfflinePackages() {
		return currentSecurityContext.isMemberOrStaffOrAdmin();
	}

    boolean shouldSeeAlphaBetaPackages(){
        return currentSecurityContext.isUser();
    }

	public void checkCanAccessReleaseVersion(ReleaseVersion releaseVersion) {
		if (releaseVersion.getReleaseType().equalsIgnoreCase("online") || shouldSeeOfflinePackages()) {
			return;
		}
		failCheck("User not authorized to access offline release version.");
	}

	public void checkCanDownloadReleaseVersion(ReleaseVersion releaseVersion) {
		if (isStaffOrAdmin()) {
			return;
		} else if (isMember()
				&& Objects.equal(releaseVersion.getReleasePackage().getMember().getKey(), Member.KEY_IHTSDO)) {
			return;
		} else if ((releaseVersion.getReleaseType().equalsIgnoreCase("online") ||(releaseVersion.getReleaseType().equalsIgnoreCase("alpha/beta")))
				&& !userStandingCalculator.isLoggedInUserAffiliateDeactivated()
				&& !userStandingCalculator.isLoggedInUserAffiliateDeregistered()
				&& !userStandingCalculator.isLoggedInUserAffiliatePendingInvoice()
				&& userMembershipAccessor.isAffiliateMemberApplicationAccepted(releaseVersion.getReleasePackage().getMember())) {
			return;
		}
		failCheck("User not authorized to download release version.");
	}


}
