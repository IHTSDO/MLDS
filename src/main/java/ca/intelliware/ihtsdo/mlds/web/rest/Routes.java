package ca.intelliware.ihtsdo.mlds.web.rest;

public class Routes {
	public static final String API_BASE_URL = "/app/rest";
	
	static final String COUNTRIES = "/app/rest/countries";
	
	/**
	 *  - get list of all submissions
	 *  - post period { startDate, endDate } to do start new submission (auto-copy from most recent)
	 */
	static final String USAGE_REPORTS = "/app/rest/licensees/{licenseeId}/commercialUsages"; 
	
	
	/** 
	 * post { submissionState: 'SUBMITTED'}
	 * post { entry : { entry json} }
	 */ 
	static final String USAGE_REPORT = "/app/rest/commercialUsages/{commercialUsageId}";
	
	/**
	 * control endpoint for single entry: put to edit, or delete
	 */
	static final String USAGE_REPORT_ENTRY = "/app/rest/commercialUsages/{commercialUsageId}/entries/{commercialUsageEntryId}"; 
}
