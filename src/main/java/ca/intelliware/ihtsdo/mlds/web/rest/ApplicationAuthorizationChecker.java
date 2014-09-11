package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.service.UserStandingCalculator;
import ca.intelliware.ihtsdo.mlds.web.rest.ApplicationResource.CreateApplicationDTO;

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

	public void checkCanCreateApplication(CreateApplicationDTO createApplicationDTO) {
		if (isAdminOrStaffOfMember(createApplicationDTO.getMemberKey())) {
			return;
		} else if (isUser() && !userStandingCalculator.isLoggedInUserAffiliateDeactivated()) {
			return;
		}
		failCheck("Not authorized to create application");
	}

}
