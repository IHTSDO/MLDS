package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;

@Service
public class ApplicationService {

	@Resource ApplicationRepository applicationRepository;

	public Application startNewApplication(ApplicationType applicationType) {
		Application application = Application.create(applicationType);
		// FIXME MLDS-308 MB fill in more details
		
		applicationRepository.save(application);
		
		return application;
	}

}
