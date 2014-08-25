'use strict';

angular.module('MLDS').controller('DeletePackageModalController', ['$scope', '$modalInstance', '$log', 'PackagesService', 'releasePackage', 
                                                       	function($scope, $modalInstance, $log, PackagesService, releasePackage) {
	$scope.releasePackage = releasePackage;
	
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.ok = function() {
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		PackagesService["delete"]($scope.releasePackage)
			.$promise.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};
}]);