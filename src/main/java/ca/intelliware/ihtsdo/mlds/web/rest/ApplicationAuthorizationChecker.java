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

}
