package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;

@Service
public class ReleasePackageAuthorizationChecker extends AuthorizationChecker {

	public void checkCanCreateReleasePackages() {
		if (isStaffOrAdmin()) {
			return;
		}
		failCheck("User not authorized to access release packages.");
	}

	public void checkCanEditReleasePackage(ReleasePackage releasePackage) {
		if (currentSecurityContext.isStaffOrAdmin()
				// FIXME MLDS-273 add back in once we have member || currentSecurityContext.isStaffFor((Member)null)
				) {
			return;
		}
		failCheck("User not authorized to access release packages.");
	}

	boolean shouldSeeOfflinePackages() {
		return currentSecurityContext.isAdmin() || currentSecurityContext.isStaff();
	}

	public void checkCanAccessReleaseVersion(ReleaseVersion releaseVersion) {
		if (releaseVersion.isOnline() || shouldSeeOfflinePackages()) {
			return;
		}
		failCheck("User not authorized to access offline release version.");
	}

}
