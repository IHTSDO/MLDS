package ca.intelliware.ihtsdo.mlds.web.rest;

public class Routes {
	static final String COUNTRIES = "/app/rest/countries";
	
	static final String USAGE_REPORTS = "/app/rest/licensees/{licenseeId}/usageReports"; // get list of all submissions including magic "current", post newSnapshotName=name to do submission
	static final String USAGE_REPORT = "/app/rest/usageReports/{usageReportId}"; // get a single list with bodies of all entries, post to add an entry
	static final String USAGE_REPORT_ENTRY = "/app/rest/usageReportEntries/{usageReportEntryPublicId}"; // control endpoint for single entry: put to edit, or delete
}
