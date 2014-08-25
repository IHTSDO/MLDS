'use strict';

angular.module('MLDS').controller('AddReleaseModalController', ['$scope', '$log', '$modalInstance', '$location', 'PackagesService', 
    function ($scope, $log, $modalInstance, $location, PackagesService) {
		$scope.releasePackage = {};
		
		$scope.submitAttempted = false;
		$scope.submitting = false;
		$scope.alerts = [];
		
		$scope.ok = function() {
			$scope.submitAttempted = true;
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			PackagesService.save($scope.releasePackage)
				.$promise.then(function(result) {
					$location.path('/releaseManagement/release/'+encodeURIComponent(result.releasePackageId));

					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
		};

}]);