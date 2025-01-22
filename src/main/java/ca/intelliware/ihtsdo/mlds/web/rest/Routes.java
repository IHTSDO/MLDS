package ca.intelliware.ihtsdo.mlds.web.rest;


public class Routes {
    public static final String API_BASE_URL = "/api";

    public static final String COUNTRIES = "/api/countries";

    public static final String MEMBERS = "/api/members";

    public static final String MEMBER = "/api/members/{memberKey}";
    // FIXME MLDS-309 spelling
    public static final String MEMBER_LICENSE = "/api/members/{memberKey}/license";

    public static final String MEMBER_LOGO = "/api/members/{memberKey}/logo";
    public static final String MEMBER_BRAND = "/api/members/{memberKey}/brand";
    public static final String MEMBER_NOTIFICATIONS = "/api/members/{memberKey}/notifications";

    public static final String AUDITS = "/api/audits";

    public static final String AUDITSEVENTS = "/api/audits/getAllAuditEvents";
    public static final String AUDITSEVENTS_CSV = "/api/audits/getAllAuditEventsCSV";
    /**
     * - get list of all submissions
     * - post period { startDate, endDate } to do start new submission (auto-copy from most recent)
     */
    static final String USAGE_REPORTS = "/api/affiliates/{affiliateId}/commercialUsages";


    /**
     * get
     * post { entry json}
     */
    static final String USAGE_REPORT = "/api/commercialUsages/{commercialUsageId}";

    /**
     * get
     */
    static final String USAGE_REPORTS_ALL = "/api/commercialUsages/";

    /**
     * put { context json}
     */
    static final String USAGE_REPORT_CONTEXT = "/api/commercialUsages/{commercialUsageId}/context";

    /**
     * put { type json}
     */
    static final String USAGE_REPORT_TYPE = "/api/commercialUsages/{commercialUsageId}/type/{type}";

    /**
     * post { transition: 'SUBMIT'}
     */
    static final String USAGE_REPORT_APPROVAL = "/api/commercialUsages/{commercialUsageId}/approval";

    /**
     * control endpoint for single entry: put to edit, or delete
     */
    static final String USAGE_REPORT_ENTRY = "/api/commercialUsages/{commercialUsageId}/entries/{commercialUsageEntryId}";

    /**
     * post { count json}
     */
    static final String USAGE_REPORT_COUNTRIES = "/api/commercialUsages/{commercialUsageId}/countries";

    /**
     * control endpoint for single count: put to edit, or delete
     */
    static final String USAGE_REPORT_COUNTRY = "/api/commercialUsages/{commercialUsageId}/countries/{commercialUsageCountId}";

    /**
     * - get list of all affiliates
     */
    static final String AFFILIATES = "/api/affiliates";

    /**
     * - get list of all affiliates that user has access to
     */
    static final String AFFILIATES_ME = "/api/affiliates/me";

    /**
     * Public affiliate check
     * - get
     */
    static final String AFFILIATES_CHECK = "/api/affiliates/check";

    /**
     * - get list of all affiliates for a username
     */
    static final String AFFILIATES_CREATOR = "/api/affiliates/creator/{username:.+}";

    /**
     * - import affiliates data from CSV
     */
    static final String AFFILIATES_CSV = "/api/affiliates/csv";
    static final String AFFILIATES_CSV_SPEC = "/api/affiliates/csvSpec";

    /**
     * - get list of all affiliates
     */
    public static final String AFFILIATE = "/api/affiliates/{affiliateId}";

    /**
     * control endpoint for application detail:
     * - GET
     * - PUT affiliate details to update details
     */
    public static final String AFFILIATE_DETAIL = "/api/affiliates/{affiliateId}/detail";

    /**
     * control endpoint for mulitple release packages:
     * - GET to get all
     * - POST to create
     */
    public static final String RELEASE_PACKAGES = "/api/releasePackages";
    public static final String ARCHIEVE_RELEASE_PACKAGES = "/api/archieveReleasePackages";

    /**
     * control endpoint for single release package:
     * - GET
     * - PUT
     * - DELETE
     */
    public static final String RELEASE_PACKAGE = "/api/releasePackages/{releasePackageId}";
    public static final String RELEASE_PACKAGE_LICENSE = "/api/releasePackages/{releasePackageId}/license";

    /**
     * control endpoint for release version within package:
     * - POST to create new
     */
    static final String RELEASE_VERSIONS = "/api/releasePackages/{releasePackageId}/releaseVersions";

    /**
     * control endpoint for single release version:
     * - GET
     * - PUT
     */
    static final String RELEASE_VERSION = "/api/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}";
    static final String RELEASE_VERSION_NOTIFICATIONS = "/api/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/notifications";
    static final String RELEASE_VERSION_DEPENDENCY = "/api/checkVersionDependency/{releaseVersionId}";
    static final String RELEASE_VERSION_ARCHIVE_UPDATE = "/api/updateArchive/{releaseVersionId}";


    /**
     * control endpoint for release files in version:
     * - POST
     */
    static final String RELEASE_FILES = "/api/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles";

    /**
     * control endpoint for single release file:
     * - GET
     * - PUT
     * - DELETE
     */
    public static final String RELEASE_FILE = "/api/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles/{releaseFileId}";

    public static final String RELEASE_FILE_DOWNLOAD = "/api/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles/{releaseFileId}/download";

    public static final String CHECK_IHTSDO_FILE = "/api/releasePackages/{releasePackageId}/releaseVersions/{releaseVersionId}/releaseFiles/{releaseFileId}/check";

    public static final String PASSWORD_RESET = "/api/passwordReset";
    public static final String PASSWORD_RESET_ITEM = "/api/passwordReset/{token}";

    /**
     * control endpoint for multiple applications:
     * - GET
     * - POST application_type to create new application
     */
    public static final String APPLICATIONS = "/api/applications";

    /**
     * control endpoint for single applications:
     * - GET
     */
    public static final String APPLICATION = "/api/applications/{applicationId}";

    /**
     * control endpoint for single applications:
     * - GET
     */
    public static final String APPLICATION_ME = "/api/applications/me";

    /**
     * control endpoint for single applications:
     * - PUT update the internal notes field
     */
    public static final String APPLICATION_NOTES_INTERNAL = "/api/applications/{applicationId}/notesInternal";

    /**
     * control endpoint for single applications:
     * - POST admin changing approve status
     */
    public static final String APPLICATION_APPROVE = "/api/applications/{applicationId}/approve";

    /**
     * FIXME control endpoint for single application in registration form:
     * - PUT affiliate user updating application registration
     * - POST affiliate user submitting for admin processing
     */
    public static final String APPLICATION_REGISTRATION = "/api/applications/{applicationId}/registration";

    /**
     * - get our version info
     */
    public static final String VERSION = "/api/version";

    /**
     * control endpoint for announcements
     * - POST announcement to send announcement to users in member's space
     */
    public static final String ANNOUNCEMENTS = "/api/announcements";

    /*MLDS 985---To Download Commercial usage CSV files*/
    public static final String REVIEW_USAGE_REPORTS = "/api/reviewUsageReports";
    /*MLDS 985---To Download Commercial usage CSV files */

    public static final String MEMBER_FEED_URL = "/api/members/{memberKey}/memberFeedUrl";

    public static final String FEED_URL = "/api/feed";


}
