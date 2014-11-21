package ca.intelliware.ihtsdo.mlds.service;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;

@Service
public class CommercialUsageAuthorizationChecker extends AuthorizationChecker {

	@Resource
	CommercialUsageRepository commercialUsageRepository;
	@Resource
	CommercialUsageEntryRepository commercialUsageEntryRepository;
	@Resource
	CommercialUsageCountryRepository commercialUsageCountRepository;

	private void checkCommercialUsageMatches(long expectedCommercialUsageEntryId, CommercialUsage commercialUsage) {
		if (commercialUsage != null) {
			if (! ObjectUtils.equals(expectedCommercialUsageEntryId, commercialUsage.getCommercialUsageId())) {
				failCheck("Commercial Usage Report and Entry have inconsistent IDs.");
			}
		}
	}

	public void checkCanAccessUsageReport(long usageReportId) {
		if (isStaffOrAdmin()) {
			return;
		}
		CommercialUsage commercialUsage = commercialUsageRepository.findOne(usageReportId);
		if (commercialUsage != null) {
			Affiliate affiliate = commercialUsage.getAffiliate();
			checkCurrentUserIsMemberOfAffiliate(affiliate);
		}
		
	}

	public void checkCanAccessCommercialUsageEntry(long commercialUsageId, long commercialUsageEntryId) {
		if (isStaffOrAdmin()) {
			return;
		}
		CommercialUsageEntry commercialUsageEntry = commercialUsageEntryRepository.findOne(commercialUsageEntryId);
		if (commercialUsageEntry != null) {
			CommercialUsage commercialUsage = commercialUsageEntry.getCommercialUsage();
			checkCommercialUsageMatches(commercialUsageId, commercialUsage);
			if (commercialUsage != null) {
				checkCurrentUserIsMemberOfAffiliate(commercialUsage.getAffiliate());
			}
		}
	
	}

	public void checkCanAccessCommercialUsageCount(long commercialUsageId, long commercialUsageCountId) {
		if (isStaffOrAdmin()) {
			return;
		}
		CommercialUsageCountry commercialUsageCount = commercialUsageCountRepository.findOne(commercialUsageCountId);
		if (commercialUsageCount != null) {
			CommercialUsage commercialUsage = commercialUsageCount.getCommercialUsage();
			checkCommercialUsageMatches(commercialUsageId, commercialUsage);
			if (commercialUsage != null) {
				checkCurrentUserIsMemberOfAffiliate(commercialUsage.getAffiliate());
			}
		}
	}

	public void checkCanReviewUsageReports() {
		if (isStaffOrAdmin()) {
			return;
		} else {
			failCheck("user does not have permissions to review usage reports.");
		}
		
	}
}
