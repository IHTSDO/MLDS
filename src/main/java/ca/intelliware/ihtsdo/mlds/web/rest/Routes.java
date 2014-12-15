package ca.intelliware.ihtsdo.mlds.web.rest;


public class Routes {
	public static final String API_BASE_URL = "/app/rest";
	
	public static final String COUNTRIES = "/app/rest/countries";
	
	public static final String MEMBERS = "/app/rest/members";
	
	// FIXME MLDS-309 spelling
	public static final String MEMBER_LICENCE = "/app/rest/members/{memberKey}/licence";
	
	public static final String MEMBER_LOGO = "/app/rest/members/{memberKey}/logo";
	public static final String MEMBER_BRAND = "/app/rest/members/{memberKey}/brand";
	
	public static final String AUDITS = "/app/rest/audits";

	
	/**
	 *  - get list of all submissions
	 *  - post period { startDate, endDate } to do start new submission (auto-copy from most recent)
	 */
	static final String USAGE_REPORTS = "/app/rest/affiliates/{affiliateId}/commercialUsages"; 
	
	
	/** 
	 * get
	 * post { entry json}
	 */ 
	static final String USAGE_REPORT = "/app/rest/commercialUsages/{commercialUsageId}";

	/** 
	 * get
	 */ 
	static final String USAGE_REPORTS_ALL = "/app/rest/commercialUsages/";
	
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
	 * - get list of all affiliates
	 */
	static final String AFFILIATES = "/app/rest/affiliates";
	
	/**
	 * - get list of all affiliates that user has access to
	 */
	static final String AFFILIATES_ME = "/app/rest/affiliates/me";

	/**
	 * - get list of all affiliates for a username
	 */
	static final String AFFILIATES_CREATOR = "/app/rest/affiliates/creator/{username:.+}";

	/**
	 * - import affiliates data from CSV
	 */
	static final String AFFILIATES_CSV = "/app/rest/affiliates/csv";
	static final String AFFILIATES_CSV_SPEC = "/app/rest/affiliates/csvSpec";
	
	/**
	 * - get list of all affiliates
	 */
	public static final String AFFILIATE = "/app/rest/affiliates/{affiliateId}";
	
	/**
	 * control endpoint for application detail:
	 * - GET
	 * - PUT affiliate details to update details
	 */
	public static final String AFFILIATE_DETAIL = "/app/rest/affiliates/{affiliateId}/detail"; 

	/**
	 * control endpoint for mulitple release packages:
	 * - GET to get all
	 * - POST to create
	 */
	public static final String RELEASE_PACKAGES = "/app/rest/releasePackages"; 
	
	/**
	 * control endpoint for single release package:
	 * - GET
	 * - PUT
	 * - DELETE
	 */
	public static final String RELEASE_PACKAGE = "/app/rest/releasePackages/{releasePackageId}"; 

	/**
	 * control endpoint for release version within package:
	 * - POST to create new
	 */
	static final String RELEASE_VERSIONS = "/app/rest/releasePackages/{releasePackageId}/releaseVersions";
	
	/**
	 * control endpoint for single release version:
	 * - GET
	 * - PUT
	 */
	static final String RELEASE_VERSION = "/app/rest/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}"; 

	/**
	 * control endpoint for release files in version:
	 * - POST
	 */
	static final String RELEASE_FILES = "/app/rest/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles";
	
	/**
	 * control endpoint for single release file:
	 * - GET
	 * - PUT
	 * - DELETE
	 */
	public static final String RELEASE_FILE = "/app/rest/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles/{releaseFileId}";
	
	public static final String RELEASE_FILE_DOWNLOAD = "/app/rest/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles/{releaseFileId}/download";
	
	
	public static final String PASSWORD_RESET = "/app/rest/passwordReset";
	public static final String PASSWORD_RESET_ITEM = "/app/rest/passwordReset/{token}";

	/**
	 * control endpoint for multiple applications:
	 * - GET
	 * - POST application_type to create new application
	 */
	public static final String APPLICATIONS = "/app/rest/applications"; 

	/**
	 * control endpoint for single applications:
	 * - GET
	 */
	public static final String APPLICATION = "/app/rest/applications/{applicationId}"; 

	/**
	 * control endpoint for single applications:
	 * - GET
	 */
	public static final String APPLICATION_ME = "/app/rest/applications/me"; 

	/**
	 * control endpoint for single applications:
	 * - PUT update the internal notes field
	 */
	public static final String APPLICATION_NOTES_INTERNAL = "/app/rest/applications/{applicationId}/notesInternal"; 

	/**
	 * control endpoint for single applications:
	 * - POST admin changing approve status
	 */
	public static final String APPLICATION_APPROVE = "/app/rest/applications/{applicationId}/approve"; 

	/**
	 * FIXME control endpoint for single application in registration form:
	 * - PUT affiliate user updating application registration
	 * - POST affiliate user submitting for admin processing
	 */
	public static final String APPLICATION_REGISTRATION = "/app/rest/applications/{applicationId}/registration";

	/**
	 * - get our version info
	 */
	public static final String VERSION = "/app/rest/version";
}
