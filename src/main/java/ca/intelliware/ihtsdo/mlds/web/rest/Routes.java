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
	 * get
	 * post { entry json}
	 */ 
	static final String USAGE_REPORT = "/app/rest/commercialUsages/{commercialUsageId}";

	/** 
	 * put { context json}
	 */ 
	static final String USAGE_REPORT_CONTEXT = "/app/rest/commercialUsages/{commercialUsageId}/context";

	/** 
	 * post { transition: 'SUBMIT'}
	 */ 
	static final String USAGE_REPORT_APPROVAL = "/app/rest/commercialUsages/{commercialUsageId}/approval";
	
	/**
	 * control endpoint for single entry: put to edit, or delete
	 */
	static final String USAGE_REPORT_ENTRY = "/app/rest/commercialUsages/{commercialUsageId}/entries/{commercialUsageEntryId}"; 

	/** 
	 * post { count json}
	 */ 
	static final String USAGE_REPORT_COUNTS = "/app/rest/commercialUsages/{commercialUsageId}/counts";
	
	/**
	 * control endpoint for single count: put to edit, or delete
	 */
	static final String USAGE_REPORT_COUNT = "/app/rest/commercialUsages/{commercialUsageId}/counts/{commercialUsageCountId}"; 


	/**
	 * - get list of all licensees that user has access to
	 */
	static final String LICENSEES_USERNAME = "/app/rest/licensees";

	public static final String REQUEST_PASSWORD_RESET = "/app/rest/requestPasswordReset";

}
