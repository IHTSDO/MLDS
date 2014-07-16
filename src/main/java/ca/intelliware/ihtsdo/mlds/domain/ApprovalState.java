package ca.intelliware.ihtsdo.mlds.domain;

public enum ApprovalState {
	//Client state
	NOT_SUBMITTED,
	CHANGE_REQUESTED,
	
	//Staff state
	SUBMITTED,
	RESUBMITTED,
	
	// Extended
	REVIEW_REQUESTED,

	// Completed
	APPROVED,
	REJECTED;
}
