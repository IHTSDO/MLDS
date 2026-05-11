package ca.intelliware.ihtsdo.mlds.service;

import ca.intelliware.ihtsdo.mlds.domain.*;
import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;
import ca.intelliware.ihtsdo.mlds.domain.application.ApplicationChangeEdit;
import ca.intelliware.ihtsdo.mlds.domain.application.ExtensionApplicationUpdateStrategy;
import ca.intelliware.ihtsdo.mlds.domain.application.UserApprovalStateChangeStrategy;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.repository.ApplicationRepository;
import ca.intelliware.ihtsdo.mlds.repository.MemberRepository;
import ca.intelliware.ihtsdo.mlds.web.SessionService;
import ca.intelliware.ihtsdo.mlds.web.rest.dto.ApplicationFilter;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String FILTER_PENDING = "approvalState/pending eq true";
    public static final String FILTER_HOME_MEMBER = "homeMember eq '(\\w+)'";
    public static final String FILTER_COUNTRY = "country eq '(\\w+)'";
    public ApplicationFilter parseFilters(List<String> filters) {

        ApplicationFilter applicationFilter = new ApplicationFilter();

        if (filters == null || filters.isEmpty() || StringUtils.isBlank(filters.get(0))) {
            return applicationFilter;
        }

        for (String filter : filters) {

            Matcher homeMemberMatcher = Pattern.compile(FILTER_HOME_MEMBER).matcher(filter);

            Matcher pendingMatcher = Pattern.compile(FILTER_PENDING).matcher(filter);

            Matcher countryMatcher = Pattern.compile(FILTER_COUNTRY).matcher(filter);

            if (homeMemberMatcher.matches()) {
                String homeMember = homeMemberMatcher.group(1);
                applicationFilter.setMember(memberRepository.findOneByKey(homeMember));
            } else if (pendingMatcher.matches()) {
                applicationFilter.setApprovalStates(
                    Arrays.asList(
                        ApprovalState.SUBMITTED,
                        ApprovalState.RESUBMITTED,
                        ApprovalState.REVIEW_REQUESTED,
                        ApprovalState.CHANGE_REQUESTED
                    )
                );
            } else if (countryMatcher.matches()) {
                applicationFilter.setCountryCode(countryMatcher.group(1));
            } else {
                throw new IllegalArgumentException("Invalid filter");
            }
        }
        return applicationFilter;
    }


    public Page<Application> findApplications(ApplicationFilter filter, Pageable pageable) {

        Member member = filter.getMember();
        List<ApprovalState> approvalStates = filter.getApprovalStates();
        String countryCode = filter.getCountryCode();

        if (member != null) {
            if (approvalStates != null) {
                if (countryCode != null) {
                    return applicationRepository
                        .findByApprovalStateInAndMemberAndAffiliateDetailsAddressCountryIsoCode2(
                            approvalStates,
                            member,
                            countryCode,
                            pageable
                        );
                }
                return applicationRepository.findByApprovalStateInAndMember(approvalStates, member, pageable);
            }
            if (countryCode != null) {
                return applicationRepository
                    .findByMemberAndAffiliateDetailsAddressCountryIsoCode2(
                        member,
                        countryCode,
                        pageable
                    );
            }
            return applicationRepository.findByMember(member, pageable);
        }
        if (approvalStates != null) {
            if (countryCode != null) {
                return applicationRepository
                    .findByApprovalStateInAndAffiliateDetailsAddressCountryIsoCode2(
                        approvalStates,
                        countryCode,
                        pageable
                    );
            }
            return applicationRepository.findByApprovalStateIn(approvalStates, pageable);
        }
        if (countryCode != null) {
            return applicationRepository
                .findByAffiliateDetailsAddressCountryIsoCode2(
                    countryCode,
                    pageable
                );
        }
        return applicationRepository.findAll(pageable);
    }

}
