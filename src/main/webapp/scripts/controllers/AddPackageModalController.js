'use strict';

angular.module('MLDS').controller('AddPackageModalController', ['$scope', '$log', '$modalInstance', '$location', 'PackagesService', 
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
					$location.path('/package/'+encodeURIComponent(result.releasePackageId));

					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					$scope.submitting = false;
				});
		};

}]);