package ca.intelliware.ihtsdo.mlds.web.rest;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ca.intelliware.ihtsdo.mlds.domain.CommercialUsage;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageCountry;
import ca.intelliware.ihtsdo.mlds.domain.CommercialUsageEntry;
import ca.intelliware.ihtsdo.mlds.domain.Affiliate;
import ca.intelliware.ihtsdo.mlds.registration.Application;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.AffiliateRepository;
import ca.intelliware.ihtsdo.mlds.service.CurrentSecurityContext;

/**
 * Provide access check helpers for our rest controllers.
 * FIXME MLDS-23
 */
@Service
public class AuthorizationChecker {

	@Resource
	AffiliateRepository affiliateRepository;
	
	@Resource
	CommercialUsageRepository commercialUsageRepository;
	
	@Resource
	CommercialUsageEntryRepository commercialUsageEntryRepository; 

	@Resource
	CommercialUsageCountryRepository commercialUsageCountRepository; 

	@Resource
	CurrentSecurityContext currentSecurityContext;
	
	private boolean isStaffOrAdmin() {
		// FIXME MLDS-256 MB consider moving this to CurrentSecurityContext
		SecurityContext securityContext = SecurityContextHolder.getContext();
		for (GrantedAuthority authority : securityContext.getAuthentication().getAuthorities()) {
			if ("ROLE_ADMIN".equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	// FIXME MLDS-256 MB inline this?
	public String getCurrentUserName() {
		return currentSecurityContext.getCurrentUserName();
	}
	
	private void failCheck(String description) {
		//FIXME which exception should actually be used? Something that turns into an appropriate HTTP security response code
		throw new IllegalStateException(description);
	}
	
	private void checkCurrentUserIsMemberOfAffiliate(Affiliate affiliate) {
		if (affiliate != null) {
			checkCurrentUserIsUser(affiliate.getCreator());
		}
	}

	private void checkCurrentUserIsUser(String username) {
		if (! ObjectUtils.equals(currentSecurityContext.getCurrentUserName(), username)) {
			//FIXME which exception should actually be used? Something that turns into an appropriate HTTP security response code
			failCheck("User not authorized to access Affiliate");
		}
	}

	private void checkCommercialUsageMatches(long expectedCommercialUsageEntryId, CommercialUsage commercialUsage) {
		if (commercialUsage != null) {
			if (! ObjectUtils.equals(expectedCommercialUsageEntryId, commercialUsage.getCommercialUsageId())) {
				failCheck("Commercial Usage Report and Entry have inconsistent IDs.");
			}
		}
	}

	public void checkCanAccessAffiliate(String username) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsUser(username);
	}


	public void checkCanAccessAffiliate(long affiliateId) {
		if (isStaffOrAdmin()) {
			return;
		}
		Affiliate Affiliate = affiliateRepository.findOne(affiliateId);
		checkCurrentUserIsMemberOfAffiliate(Affiliate);
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

	public void checkCanAccessReleasePackages() {
		if (isStaffOrAdmin()) {
			return;
		}
		failCheck("User not authorized to access release packages.");
	}

	public void checkCanAccessApplication(Application application) {
		if (isStaffOrAdmin()) {
			return;
		}
		checkCurrentUserIsUser(application.getUsername());
	}
}
