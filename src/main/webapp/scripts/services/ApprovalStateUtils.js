'use strict';

mldsApp.factory('ApprovalStateUtils', [function(){

	// complete, waiting for applicant, waiting for staff
	

		var service = {};
			
		service.isWaitingForApplicant = function isWaitingForApplicant(approvalState) {
			return (!approvalState
				|| approvalState === 'NOT_SUBMITTED'
				|| approvalState === 'CHANGE_REQUESTED');
		};

		service.isApproved = function isApproved(approvalState) {
			return (approvalState === 'APPROVED');
		};

		service.isRejected = function isRejected(approvalState) {
			return (approvalState === 'REJECTED');
		};

		service.isIncomplete = function isIncomplete(approvalState) {
			return (approvalState !== 'APPROVED'
				&& approvalState !== 'REJECTED');
		};
			
		service.isPending = function isPending(approvalState) {
			return (approvalState === 'SUBMITTED'
				|| approvalState === 'RESUBMITTED'
				|| approvalState === 'REVIEW_REQUESTED');
		};

		return service;
	}]);