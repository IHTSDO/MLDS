'use strict';

mldsApp.factory('StandingStateUtils', [function(){

		var service = {};

		service.isApplying = function isApplying(standingState) {
			return (standingState === 'APPLYING');
		};

		service.isRejected = function isRejected(standingState) {
			return (standingState === 'REJECTED');
		};

		service.isDeactivated = function isDeactivated(standingState) {
			return (standingState === 'DEACTIVATED');
		};
		
		service.isDeactivationPending = function isDeactivationPending(standingState) {
			return (standingState === 'DEACTIVATION_PENDING');
		};

		service.isDeregistrationPending = function isDeregistrationPending(standingState) {
			return (standingState === 'DEREGISTRATION_PENDING');
		};

		service.isSuccessCategory = function isSuccessCategory(standingState) {
			return (standingState === 'IN_GOOD_STANDING');
		};

		service.isWarningCategory = function isWarningCategory(standingState) {
			return (standingState === 'APPLYING'
					|| standingState === 'DEACTIVATION_PENDING'
					|| standingState === 'DEREGISTRATION_PENDING');
		};

		service.isDangerCategory = function isDangerCategory(standingState) {
			return (standingState === 'REJECTED'
				|| standingState === 'DEACTIVATED'
				|| standingState === 'DEREGISTERED');
		};

		return service;
	}]);