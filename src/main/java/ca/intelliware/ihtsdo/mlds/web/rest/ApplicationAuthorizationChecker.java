package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Application;

@Service
public class ApplicationAuthorizationChecker extends AuthorizationChecker {
	public void checkCanAccessApplication(Application application) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsUser(application.getUsername());
	}

	public void checkCanApproveApplication(Application application) {
		if (isAdminOrStaffOfMember(application.getMember().getKey())) {
			return;
		}
		failCheck("Not authorized to approve application");
	}

}
