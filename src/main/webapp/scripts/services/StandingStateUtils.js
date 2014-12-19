'use strict';

mldsApp.factory('StandingStateUtils', [function(){

		var service = {};

		service.isApplying = function isApplying(standingState) {
			return (standingState === 'APPLYING');
		};

		service.isRejected = function isRejected(standingState) {
			return (standingState === 'REJECTED');
		};
		
		service.wasApproved = function wasApproved(standingState) {
			return (standingState !== 'APPLYING' && standingState !== 'REJECTED');
		};

		service.isDeactivated = function isDeactivated(standingState) {
			return (standingState === 'DEACTIVATED');
		};
		
		service.isDeactivationPending = function isDeactivationPending(standingState) {
			return (standingState === 'DEACTIVATION_PENDING');
		};

		service.isPendingInvoice = function isPendingInvoice(standingState) {
			return (standingState === 'PENDING_INVOICE' || standingState === 'INVOICE_SENT');
		};
		
		service.isInvoiceSent = function isInvoiceSent(standingState) {
			return (standingState === 'INVOICE_SENT');
		};
		
		service.isSuccessCategory = function isSuccessCategory(standingState) {
			return (standingState === 'IN_GOOD_STANDING');
		};

		service.isWarningCategory = function isWarningCategory(standingState) {
			return (standingState === 'APPLYING'
					|| standingState === 'PENDING_INVOICE'
					|| standingState === 'INVOICE_SENT'
					|| standingState === 'DEACTIVATION_PENDING');
		};

		service.isDangerCategory = function isDangerCategory(standingState) {
			return (standingState === 'REJECTED'
				|| standingState === 'DEACTIVATED'
				|| standingState === 'DEREGISTERED');
		};

		return service;
	}]);