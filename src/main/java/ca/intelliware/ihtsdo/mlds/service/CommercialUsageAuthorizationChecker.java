package ca.intelliware.ihtsdo.mlds.service;


import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.security.ihtsdo.AuthorizationChecker;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<CommercialUsage> optionalCommercialUsage = commercialUsageRepository.findById(usageReportId);
        if (optionalCommercialUsage.isPresent()) {
            CommercialUsage commercialUsage = optionalCommercialUsage.get();
			Affiliate affiliate = commercialUsage.getAffiliate();
			checkCurrentUserIsMemberOfAffiliate(affiliate);
		}

	}

	public void checkCanAccessCommercialUsageEntry(long commercialUsageId, long commercialUsageEntryId) {
		if (isStaffOrAdmin()) {
			return;
		}

        Optional<CommercialUsageEntry> optionalCommercialUsageEntry = commercialUsageEntryRepository.findById(commercialUsageEntryId);
        if (optionalCommercialUsageEntry.isPresent()) {
            CommercialUsageEntry commercialUsageEntry = optionalCommercialUsageEntry.get();
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

        Optional<CommercialUsageCountry> optionalCommercialUsageCount = commercialUsageCountRepository.findById(commercialUsageCountId);
        if (optionalCommercialUsageCount.isPresent()) {
            CommercialUsageCountry commercialUsageCount = optionalCommercialUsageCount.get();
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
