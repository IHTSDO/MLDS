package ca.intelliware.ihtsdo.mlds.domain.application;

import org.joda.time.Instant;

import ca.intelliware.ihtsdo.mlds.domain.Application;
import ca.intelliware.ihtsdo.mlds.domain.ApprovalState;

public class UserApprovalStateChangeStrategy implements ApplicationChangeEdit {
	@Override
	public void applyChangeOrFail(Application original, Application updatedApplication) {
		if (updatedApplication.getApprovalState() != original.getApprovalState()) {
			ApprovalState originalState = original.getApprovalState();
			ApprovalState updatedState = updatedApplication.getApprovalState();
			if (originalState == ApprovalState.NOT_SUBMITTED && updatedState == ApprovalState.SUBMITTED) {
				original.setApprovalState(ApprovalState.SUBMITTED);
				original.setSubmittedAt(Instant.now());
			} else if (originalState == ApprovalState.CHANGE_REQUESTED && updatedState == ApprovalState.SUBMITTED) {
				original.setApprovalState(ApprovalState.RESUBMITTED);
				original.setSubmittedAt(Instant.now());
			} else if (originalState == ApprovalState.CHANGE_REQUESTED && updatedState == ApprovalState.RESUBMITTED) {
				original.setApprovalState(ApprovalState.RESUBMITTED);
				original.setSubmittedAt(Instant.now());
			} else {
				throw new IllegalArgumentException("approvalState: can't change from " + original.getApprovalState() + " to " + updatedApplication.getApprovalState());
			}
		}
	}
}