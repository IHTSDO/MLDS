package ca.intelliware.ihtsdo.mlds.web.rest;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.UserStandingCalculator;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;



@Service
public class ApplicationAuthorizationChecker extends AuthorizationChecker {

	@Resource
    UserStandingCalculator userStandingCalculator;

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

	public void checkCanCreateApplication(ApplicationResource.CreateApplicationDTO createApplicationDTO) {
		if (isAdminOrStaffOfMember(createApplicationDTO.getMemberKey())) {
			return;
		} else if (isUser() && !userStandingCalculator.isLoggedInUserAffiliateDeactivated()) {
			return;
		}
		failCheck("Not authorized to create application");
	}

}
