'use strict';

angular.module('MLDS').controller('ChangeRequestedModalController', 
		['$scope', '$log', '$modalInstance', 'UserRegistrationService', 'application',
		 function($scope, $log,  $modalInstance, UserRegistrationService, application) {
	
	$scope.application = application;
	
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.ok = function(form) {
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		UserRegistrationService.approveApplication(application, 'CHANGE_REQUESTED')
			.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};

}]);