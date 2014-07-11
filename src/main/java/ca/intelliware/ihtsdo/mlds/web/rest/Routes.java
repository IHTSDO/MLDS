package ca.intelliware.ihtsdo.mlds.web.rest;

public class Routes {
	public static final String API_BASE_URL = "/app/rest";
	
	public static final String COUNTRIES = "/app/rest/countries";
	
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
	 * put { type json}
	 */ 
	static final String USAGE_REPORT_TYPE = "/app/rest/commercialUsages/{commercialUsageId}/type/{type}";

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
	static final String USAGE_REPORT_COUNTRIES = "/app/rest/commercialUsages/{commercialUsageId}/countries";
	
	/**
	 * control endpoint for single count: put to edit, or delete
	 */
	static final String USAGE_REPORT_COUNTRY = "/app/rest/commercialUsages/{commercialUsageId}/countries/{commercialUsageCountId}"; 


	/**
	 * - get list of all licensees that user has access to
	 */
	static final String LICENSEES_USERNAME = "/app/rest/licensees";

	
	/**
	 * control endpoint for mulitple release packages:
	 * - GET to get all
	 * - POST to create
	 */
	static final String RELEASE_PACKAGES = "/app/rest/releasePackages"; 
	
	/**
	 * control endpoint for single release package:
	 * - GET
	 * - PUT
	 * - DELETE
	 */
	static final String RELEASE_PACKAGE = "/app/rest/releasePackages/{releasePackageId}"; 

	/**
	 * control endpoint for single release version:
	 * - GET
	 * - PUT
	 */
	static final String RELEASE_VERSION = "/app/rest/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}"; 

	/**
	 * control endpoint for single release file:
	 * - GET
	 * - PUT
	 * - DELETE
	 */
	static final String RELEASE_FILE = "/app/rest/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles/{releaseFileId}"; 
	
	
	public static final String PASSWORD_RESET = "/app/rest/passwordReset";
	public static final String PASSWORD_RESET_ITEM = "/app/rest/passwordReset/{token}";


}
