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
import ca.intelliware.ihtsdo.mlds.domain.Licensee;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageCountryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageEntryRepository;
import ca.intelliware.ihtsdo.mlds.repository.CommercialUsageRepository;
import ca.intelliware.ihtsdo.mlds.repository.LicenseeRepository;

/**
 * Provide access check helpers for our rest controllers.
 * FIXME MLDS-23
 */
@Service
public class AuthorizationChecker {

	@Resource
	LicenseeRepository licenseeRepository;
	
	@Resource
	CommercialUsageRepository commercialUsageRepository;
	
	@Resource
	CommercialUsageEntryRepository commercialUsageEntryRepository; 

	@Resource
	CommercialUsageCountryRepository commercialUsageCountRepository; 

	private boolean isStaffOrAdmin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		for (GrantedAuthority authority : securityContext.getAuthentication().getAuthorities()) {
			if ("ROLE_ADMIN".equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	private String currentUserName() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return securityContext.getAuthentication().getName();
	}
	
	private void failCheck(String description) {
		//FIXME which exception should actually be used? Something that turns into an appropriate HTTP security response code
		throw new IllegalStateException(description);
	}
	
	private void checkCurrentUserIsMemberOfLicensee(Licensee licensee) {
		if (licensee != null) {
			if (! ObjectUtils.equals(currentUserName(), licensee.getCreator())) {
				//FIXME which exception should actually be used? Something that turns into an appropriate HTTP security response code
				failCheck("User not authorized to access Licensee");
			}
		}
	}
	
	private void checkCommercialUsageMatches(long expectedCommercialUsageEntryId, CommercialUsage commercialUsage) {
		if (commercialUsage != null) {
			if (! ObjectUtils.equals(expectedCommercialUsageEntryId, commercialUsage.getCommercialUsageId())) {
				failCheck("Commercial Usage Report and Entry have inconsistent IDs.");
			}
		}
	}


	public void checkCanAccessLicensee(long licenseeId) {
		if (isStaffOrAdmin()) {
			return;
		}
		Licensee licensee = licenseeRepository.findOne(licenseeId);
		checkCurrentUserIsMemberOfLicensee(licensee);
	}

	public void checkCanAccessUsageReport(long usageReportId) {
		if (isStaffOrAdmin()) {
			return;
		}
		CommercialUsage commercialUsage = commercialUsageRepository.findOne(usageReportId);
		if (commercialUsage != null) {
			Licensee licensee = commercialUsage.getLicensee();
			checkCurrentUserIsMemberOfLicensee(licensee);
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
				checkCurrentUserIsMemberOfLicensee(commercialUsage.getLicensee());
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
				checkCurrentUserIsMemberOfLicensee(commercialUsage.getLicensee());
			}
		}
	}

	public void checkCanAccessReleasePackage(long packageId) {
		if (isStaffOrAdmin()) {
			return;
		}
		failCheck("User not authorized to access release package.");
	}

	public void checkCanAccessReleaseVersion(long releasePackageId, long releaseVersionId) {
		//FIXME should we check children being consistent?
		checkCanAccessReleasePackage(releasePackageId);		
	}

	public void checkCanAccessReleaseFile(long releasePackageId, long releaseVersionId, long releaseFileId) {
		//FIXME should we check children being consistent?
		checkCanAccessReleasePackage(releasePackageId);		
	}

}
