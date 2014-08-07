package ca.intelliware.ihtsdo.mlds.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.Member;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;

@Service
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
		
		applicationRepository.save(application);
		
		if (applicationType.equals(ApplicationType.EXTENSION)){
			List<Affiliate> affiliates = affiliateRepository.findByCreator(sessionService.getUsernameOrNull());
			Affiliate affiliate = affiliates.get(0);
			affiliate.addApplication(application);
			affiliateRepository.save(affiliate);
		}
		return application;
	}

}
