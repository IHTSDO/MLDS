'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', ['$scope', '$modalInstance', '$log', 'releasePackage',
                                                       	function($scope, $modalInstance, $log, releasePackage) {
	
	$scope.releasePackage = releasePackage;
	
	$scope.submitAttempted = false;
	$scope.alerts = [];
	
	$scope.add = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);

	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	

}]);