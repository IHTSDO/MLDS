package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.joda.time.Instant;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.UsageReportState;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;

@Service
@Transactional
public class CommercialUsageService {
	
	@Resource CommercialUsageRepository commercialUsageRepository;
	@Resource CommercialUsageResetter commercialUsageResetter;
	@Resource CommercialUsageAuthorizationChecker authorizationChecker;
	@Resource CommercialUsageAuditEvents commercialUsageAuditEvents;

	public CommercialUsage transitionCommercialUsageApproval(CommercialUsage commercialUsage, UsageReportTransition transition) {
    	// FIXME Extract out to "workflow" handler
		if (UsageReportState.NOT_SUBMITTED.equals(commercialUsage.getState()) && UsageReportTransition.SUBMIT.equals(transition)
				&& commercialUsage.isActive()) {
    		commercialUsage = submitUsage(commercialUsage);
		} else if (UsageReportState.CHANGE_REQUESTED.equals(commercialUsage.getState()) && UsageReportTransition.SUBMIT.equals(transition)
				&& commercialUsage.isActive()) {
        	commercialUsage = resubmitUsage(commercialUsage);
		} else if ((UsageReportState.SUBMITTED.equals(commercialUsage.getState())
					|| UsageReportState.RESUBMITTED.equals(commercialUsage.getState()) 
					|| UsageReportState.PENDING_INVOICE	.equals(commercialUsage.getState())
					|| UsageReportState.INVOICE_SENT.equals(commercialUsage.getState())
					|| UsageReportState.PAID.equals(commercialUsage.getState())
				   ) 
					&& UsageReportTransition.RETRACT.equals(transition) && commercialUsage.isActive()) {
    		commercialUsage = retractUsage(commercialUsage);
		} else if ((UsageReportState.SUBMITTED.equals(commercialUsage.getState()) || UsageReportState.RESUBMITTED.equals(commercialUsage
				.getState())) && UsageReportTransition.PENDING_INVOICE.equals(transition) && commercialUsage.isActive()) {
    		commercialUsage = setState(commercialUsage, UsageReportState.PENDING_INVOICE);
    	} else if (UsageReportState.PENDING_INVOICE.equals(commercialUsage.getState()) 
    				&& UsageReportTransition.INVOICE_SENT.equals(transition) 
    				&& commercialUsage.isActive()) {
    		commercialUsage = setState(commercialUsage, UsageReportState.INVOICE_SENT);
    	} else if (UsageReportState.INVOICE_SENT.equals(commercialUsage.getState()) 
    				&& UsageReportTransition.PAID.equals(transition) 
    				&& commercialUsage.isActive()) {
			commercialUsage = setState(commercialUsage, UsageReportState.PAID);
    	} else {
			throw new IllegalStateException("Unsupported usage report transition of" + transition.name() + " while in state " + commercialUsage.getState().name());
    	}
        
		commercialUsageAuditEvents.logUsageReportStateChange(commercialUsage);
    	
    	return commercialUsage;
	}

	private CommercialUsage setState(CommercialUsage commercialUsage, UsageReportState newState) {
		authorizationChecker.checkCanReviewUsageReports();
		commercialUsage.setState(newState);
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}

	private CommercialUsage retractUsage(CommercialUsage commercialUsage) {
		// Mark original usage as no-longer effective
		commercialUsage.setEffectiveTo(Instant.now());
		commercialUsage = commercialUsageRepository.saveAndFlush(commercialUsage);
		// Create duplicate usage to replace original and become the active one
		commercialUsageResetter.detachAndReset(commercialUsage, commercialUsage.getStartDate(), commercialUsage.getEndDate());
		commercialUsage.setState(UsageReportState.CHANGE_REQUESTED);
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}

	private CommercialUsage submitUsage(CommercialUsage commercialUsage) {
		commercialUsage.setState(UsageReportState.SUBMITTED);
		commercialUsage.setSubmitted(Instant.now());
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}

	private CommercialUsage resubmitUsage(CommercialUsage commercialUsage) {
		commercialUsage.setState(UsageReportState.RESUBMITTED);
		commercialUsage.setSubmitted(Instant.now());
		commercialUsage = commercialUsageRepository.save(commercialUsage);
		return commercialUsage;
	}
}
