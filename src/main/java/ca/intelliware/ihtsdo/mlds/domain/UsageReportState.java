package ca.intelliware.ihtsdo.mlds.domain;

public enum UsageReportState {
	//Client state
	NOT_SUBMITTED,
	CHANGE_REQUESTED,
	
	//Staff state
	SUBMITTED,
	RESUBMITTED,
	PENDING_INVOICE, 
	INVOICE_SENT,
	
	// Extended
	REVIEW_REQUESTED,

	// Completed
	PAID,
	REJECTED;
}
