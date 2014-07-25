package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.registration.Application;

@Service
public class ApplicationAuthorizationChecker extends AuthorizationChecker {
	public void checkCanAccessApplication(Application application) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsUser(application.getUsername());
	}

	public void checkCanApproveApplication(Application application) {
		// FIXME MLDS-372 check member of application.
		if (currentSecurityContext.isAdmin() || currentSecurityContext.isStaff()) {
			return;
		}
		failCheck("Not authorized to approve application");
	}

}
