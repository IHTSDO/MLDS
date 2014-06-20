package ca.intelliware.ihtsdo.mlds.web.rest;

public class Routes {
	static final String COUNTRIES = "/app/rest/countries";
	
	static final String USAGE_REPORTS = "/app/rest/licensees/{licenseeId}/commercialUsages"; // get list of all submissions , post period to do start new submission (auto-copy from most recent)
	static final String USAGE_REPORT = "/app/rest/commercialUsages/{commercialUsageId}"; // PUT to change submission state
	// post { submissionState: 'SUBMITTED'}
	// post { entry : { /* entry json */} }
	static final String USAGE_REPORT_ENTRY = "/app/rest/commercialUsageEntries/{commercialUsageEntryId}"; // control endpoint for single entry: put to edit, or delete
}
