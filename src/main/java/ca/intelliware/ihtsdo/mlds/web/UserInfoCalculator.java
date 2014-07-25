package ca.intelliware.ihtsdo.mlds.web;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.base.Objects;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;

@Service
public class UserInfoCalculator {
	@Resource
	SessionService sessionService;
	
	@Resource
	ApplicationRepository applicationRepository;
	
	public UserInfo createUserInfo() {
		UserInfo userInfo = new UserInfo();
		
		String usernameOrNull = sessionService.getUsernameOrNull();
		if (usernameOrNull == null) {
			userInfo.setHasApplied(false);
			userInfo.setApproved(false);
		} else {
			List<Application> applications = applicationRepository.findByUsername(usernameOrNull);
			boolean hasApplied = !applications.isEmpty() 
					&& !Objects.equal(applications.get(0).getApprovalState(), ApprovalState.NOT_SUBMITTED)
					&& !Objects.equal(applications.get(0).getApprovalState(), ApprovalState.CHANGE_REQUESTED);
			userInfo.setHasApplied(hasApplied);
			userInfo.setApproved(hasApplied 
					&& Objects.equal(applications.get(0).getApprovalState(), ApprovalState.APPROVED));
			//FIXME rejected?
		}
		return userInfo;
	}
	
}