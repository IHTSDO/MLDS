package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import ca.intelliware.ihtsdo.mlds.web.rest.UserMembershipAccessor;

@Service
public class ApplicationService {

	@Resource ApplicationRepository applicationRepository;
	@Resource SessionService sessionService;
	@Resource UserMembershipAccessor userMembershipAccessor; 


	public Application startNewApplication(ApplicationType applicationType) {
		Application application = Application.create(applicationType);
		
		application.setUsername(sessionService.getUsernameOrNull());
		application.setMember(userMembershipAccessor.getMemberAssociatedWithUser());
		// FIXME MLDS-308 MB fill in more details
		
		applicationRepository.save(application);
		
		return application;
	}

}
