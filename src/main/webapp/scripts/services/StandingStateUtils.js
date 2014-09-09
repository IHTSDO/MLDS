'use strict';

mldsApp.factory('StandingStateUtils', [function(){

		var service = {};
			
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