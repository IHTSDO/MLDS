package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.joda.time.Instant;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;

@Service
@Transactional
public class CommercialUsageService {
	
	@Resource CommercialUsageRepository commercialUsageRepository;
	@Resource CommercialUsageResetter commercialUsageResetter;
	@Resource CommercialUsageAuthorizationChecker authorizationChecker;
	@Resource CommercialUsageAuditEvents commercialUsageAuditEvents;

	public CommercialUsage transitionCommercialUsageApproval(CommercialUsage commercialUsage, ApprovalTransition transition) {
    	// FIXME Extract out to "workflow" handler
    	if (ApprovalState.NOT_SUBMITTED.equals(commercialUsage.getApprovalState()) && ApprovalTransition.SUBMIT.equals(transition)  && commercialUsage.isActive()) {
    		commercialUsage = submitUsage(commercialUsage);
    	} else if (ApprovalState.CHANGE_REQUESTED.equals(commercialUsage.getApprovalState()) && ApprovalTransition.SUBMIT.equals(transition)  && commercialUsage.isActive()) {
        	commercialUsage = resubmitUsage(commercialUsage);
    	} else if ((ApprovalState.SUBMITTED.equals(commercialUsage.getApprovalState()) || ApprovalState.RESUBMITTED.equals(commercialUsage.getApprovalState()) || ApprovalState.APPROVED.equals(commercialUsage.getApprovalState())) && ApprovalTransition.RETRACT.equals(transition) && commercialUsage.isActive()) {
    		commercialUsage = retractUsage(commercialUsage);
    	} else if ((ApprovalState.SUBMITTED.equals(commercialUsage.getApprovalState()) || ApprovalState.RESUBMITTED.equals(commercialUsage.getApprovalState())) && ApprovalTransition.REVIEWED.equals(transition)  && commercialUsage.isActive()) {
    		commercialUsage = reviewedUsage(commercialUsage);
    	} else {
    		throw new IllegalStateException("Unsupported approval transition");
    	}
        
    	commercialUsageAuditEvents.logApprovalStateChange(commercialUsage);
    	
    	return commercialUsage;
	}

	private CommercialUsage reviewedUsage(CommercialUsage commercialUsage) {
		authorizationChecker.checkCanReviewUsageReports();
		commercialUsage.setApprovalState(ApprovalState.APPROVED);
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}

	private CommercialUsage retractUsage(CommercialUsage commercialUsage) {
		// Mark original usage as no-longer effective
		commercialUsage.setEffectiveTo(Instant.now());
		commercialUsage = commercialUsageRepository.saveAndFlush(commercialUsage);
		// Create duplicate usage to replace original and become the active one
		commercialUsageResetter.detachAndReset(commercialUsage, commercialUsage.getStartDate(), commercialUsage.getEndDate());
		commercialUsage.setApprovalState(ApprovalState.CHANGE_REQUESTED);
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}

	private CommercialUsage submitUsage(CommercialUsage commercialUsage) {
		commercialUsage.setApprovalState(ApprovalState.SUBMITTED);
		commercialUsage.setSubmitted(Instant.now());
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}

	private CommercialUsage resubmitUsage(CommercialUsage commercialUsage) {
		commercialUsage.setApprovalState(ApprovalState.RESUBMITTED);
		commercialUsage.setSubmitted(Instant.now());
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}
}
