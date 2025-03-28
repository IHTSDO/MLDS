package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.AffiliateDetails;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.domain.application.ApplicationChangeEdit;
import ca.intelliware.ihtsdo.mlds.domain.application.ExtensionApplicationUpdateStrategy;
import ca.intelliware.ihtsdo.mlds.domain.application.UserApprovalStateChangeStrategy;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
public class ApplicationService {

	@Resource ApplicationRepository applicationRepository;
	@Resource SessionService sessionService;
	@Resource AffiliateRepository affiliateRepository;
	@Resource MemberRepository memberRepository;

	public Application startNewApplication(ApplicationType applicationType, Member member) {
		Application application = Application.create(applicationType);

		application.setUsername(sessionService.getUsernameOrNull());
		application.setMember(member);
		// FIXME MLDS-308 MB fill in more details
		// FIXME MLDS-310 MB copy affiliate details

		applicationRepository.save(application);

		if (applicationType.equals(ApplicationType.EXTENSION)){
			// FIXME MLDS-308 MB should the affiliate be passed in?
			List<Affiliate> affiliates = affiliateRepository.findByCreatorIgnoreCase(sessionService.getUsernameOrNull());
			Affiliate affiliate = affiliates.get(0);
			affiliate.addApplication(application);
			AffiliateDetails detailsCopy = affiliate.getAffiliateDetails().copyNoId();
			application.setAffiliateDetails(detailsCopy);
//			affiliateRepository.save(affiliate);
		}
		return application;
	}


	/**
	 * Apply changes from updated to original or generate IllegalArgumentException
	 * @param original
	 * @param updatedApplication
	 * @throws IllegalArgumentException
	 */
	public void doUpdate(Application original, Application updatedApplication) throws IllegalArgumentException {
		new ExtensionApplicationUpdateStrategy().applyChangeOrFail(original, updatedApplication);

		ApplicationChangeEdit stateChangeStrategy = new UserApprovalStateChangeStrategy();
		//FIXME MLDS-310 Implement StaffApprovalStateChangeStrategy
		stateChangeStrategy.applyChangeOrFail(original, updatedApplication);
	}

}
