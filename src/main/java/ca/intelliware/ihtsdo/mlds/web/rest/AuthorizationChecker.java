package ca.intelliware.ihtsdo.mlds.web.rest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provide access check helpers for our rest controllers.
 * FIXME MLDS-23
 */
@Service
public class AuthorizationChecker {

	public void checkCanAccessLicensee(long licenseeId) {
		SecurityContextHolder.getContext();
		// if admin, staff, or user attached to licensee, do ...
	}

	public void checkCanAccessUsageReport(long usageReportId) {
		// TODO Auto-generated method stub
	}

	public void checkCanAccessCommercialUsageEntry(long commercialUsageEntryId) {
		// TODO Auto-generated method stub
	}

}
