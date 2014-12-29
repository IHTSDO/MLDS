'use strict';

mldsApp.factory('StandingStateUtils', [function(){

		var service = {};
		
		service.options = function() {
			return ['APPLYING', 'REJECTED', 'IN_GOOD_STANDING', 'DEACTIVATED', 'DEACTIVATION_PENDING', 'PENDING_INVOICE', 'DEREGISTERED'];
		};

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
			return (standingState === 'PENDING_INVOICE');
		};

		service.isSuccessCategory = function isSuccessCategory(standingState) {
			return (standingState === 'IN_GOOD_STANDING');
		};

		service.isWarningCategory = function isWarningCategory(standingState) {
			return (standingState === 'APPLYING'
					|| standingState == 'PENDING_INVOICE'
					|| standingState === 'DEACTIVATION_PENDING');
		};

		service.isDangerCategory = function isDangerCategory(standingState) {
			return (standingState === 'REJECTED'
				|| standingState === 'DEACTIVATED'
				|| standingState === 'DEREGISTERED');
		};

		return service;
	}]);