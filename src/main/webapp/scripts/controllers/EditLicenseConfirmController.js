'use strict';

angular.module('MLDS').controller('EditLicenseConfirmController', ['$scope', '$log', '$modalInstance', '$location', 'currentMember', 
    function ($scope, $log, $modalInstance, $location, currentMember) {
		$scope.currentMember = currentMember;
		
		$scope.submitAttempted = false;
		$scope.submitting = false;
		$scope.alerts = [];

		$scope.ok = function() {
			$scope.submitAttempted = true;
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			$modalInstance.close('ok');
		};

}]);