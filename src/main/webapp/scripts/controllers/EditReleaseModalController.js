'use strict';

angular.module('MLDS').controller('EditReleaseModalController', ['$scope', '$log', '$modalInstance', '$location', 'PackagesService', 'releasePackage', 
    function ($scope, $log, $modalInstance, $location, PackagesService, releasePackage) {
		$scope.releasePackage = releasePackage;
		
		$scope.submitAttempted = false;
		$scope.submitting = false;
		$scope.alerts = [];
		
		$scope.ok = function() {
			$scope.submitAttempted = true;
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			PackagesService.update($scope.releasePackage)
				.$promise.then(function(result) {
					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					$scope.submitting = false;
				});
		};

}]);