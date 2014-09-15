'use strict';

angular.module('MLDS').controller('StartExtensionApplicationModalController', 
		['$scope', '$log', '$modalInstance', 'UserRegistrationService', 'releasePackage',
		 function($scope, $log,  $modalInstance, UserRegistrationService, releasePackage) {
	
	$scope.releasePackage = releasePackage;
	
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.ok = function() {
		$log.log('make call');
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		UserRegistrationService.createExtensionApplication(releasePackage.member)
			.then(function(result) {
				$log.log('result', result);
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};

}]);