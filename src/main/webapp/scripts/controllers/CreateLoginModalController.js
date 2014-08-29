'use strict';

angular.module('MLDS').controller('CreateLoginModalController',
		['$scope', '$log', '$modalInstance', 'affiliate', 'reason',
    function ($scope, $log, $modalInstance, affiliate, reason) {
		$log.log('create login modal controller!', reason);
		$scope.affiliate = affiliate;
		$scope.submitAttempted = false;
		
		$scope.alerts = [];
		
		if (reason.noEmail) {
			$scope.alerts.push({type: 'danger', msg: 'There is currently no email assigned to the affiliate.'});
		}
		
		if (reason.duplicateEmail) {
			$scope.alerts.push({type: 'danger', msg: 'There is already another affiliate with the same email.'});
		}
		
		$scope.oldEmail = $scope.affiliate.affiliateDetails.email;
		
		$scope.ok = function() {
			$scope.submitting = true;
		};
		
    }]);



