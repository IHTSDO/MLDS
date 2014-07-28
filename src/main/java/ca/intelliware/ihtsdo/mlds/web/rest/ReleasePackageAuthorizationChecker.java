package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.ReleasePackage;
import ca.intelliware.ihtsdo.mlds.domain.ReleaseVersion;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;

@Service
public class ReleasePackageAuthorizationChecker extends AuthorizationChecker {
	@Resource
	MemberRepository memberRepository;

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
		return currentSecurityContext.isAdmin() || currentSecurityContext.isStaff();
	}

	public void checkCanAccessReleaseVersion(ReleaseVersion releaseVersion) {
		if (releaseVersion.isOnline() || shouldSeeOfflinePackages()) {
			return;
		}
		failCheck("User not authorized to access offline release version.");
	}

	public Member getMemberRepresentedByUser() {
		String memberKey = currentSecurityContext.getStaffMemberKey();
		return memberRepository.findOneByKey(memberKey);
	}

}
