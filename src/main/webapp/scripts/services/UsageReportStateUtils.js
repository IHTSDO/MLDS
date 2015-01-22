'use strict';

mldsApp.factory('UsageReportStateUtils', [function(){

	//	NOT_SUBMITTED, CHANGE_REQUESTED, SUBMITTED, RESUBMITTED, PENDING_INVOICE, INVOICE_SENT,
	//	REVIEW_REQUESTED,PAID,REJECTED

		var service = {};
		
		service.isWaitingForApplicant = function isWaitingForApplicant(usageState) {
			return (!usageState
				|| usageState === 'NOT_SUBMITTED'
				|| usageState === 'CHANGE_REQUESTED');
		};

		service.isInvoiceSent = function isInvoiceSent(usageState) {
			return (usageState === 'INVOICE_SENT');
		};
		
		service.isPendingInvoice = function isPendingInvoice(usageState) {
			return (usageState === 'PENDING_INVOICE');
		};

		service.isRejected = function isRejected(usageState) {
			return (usageState === 'REJECTED');
		};
			
		service.isSubmitted = function isSubmitted(usageState) {
			return (usageState === 'SUBMITTED'
				|| usageState === 'RESUBMITTED');
		};

		return service;
	}]);