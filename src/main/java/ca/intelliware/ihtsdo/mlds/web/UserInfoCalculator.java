package ca.intelliware.ihtsdo.mlds.web;

import java.util.List;

import javax.annotation.Resource;

import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.registration.ApplicationRepository;

public class UserInfoCalculator {
	@Resource
	SessionService sessionService;
	
	@Resource
	ApplicationRepository applicationRepository;
	
	UserInfo createUserInfo() {
		UserInfo userInfo = new UserInfo();
		
		String usernameOrNull = sessionService.getUsernameOrNull();
		if (usernameOrNull == null) {
			userInfo.setHasApplied(false);
			userInfo.setApproved(false);
		} else {
			List<Application> applications = applicationRepository.findByUsername(usernameOrNull);
			boolean hasApplied = !applications.isEmpty();
			userInfo.setHasApplied(hasApplied);
			userInfo.setApproved(hasApplied && applications.get(0).isApproved());
		}
		return userInfo;
	}
	
}