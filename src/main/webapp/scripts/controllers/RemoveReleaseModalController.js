'use strict';

angular.module('MLDS').controller('RemoveReleaseModalController', 
		['$scope', '$modalInstance',  '$log', 'PackagesService', 'releasePackage',
       	function($scope, $modalInstance, $log, PackagesService, releasePackage) {
			
	$log.log('RemoveReleaseModalController');
	$scope.alerts = [];
	$scope.submitting = false;
	$scope.releasePackage = releasePackage;
	
	$scope.ok = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		PackagesService.remove($scope.releasePackage)
			.$promise.then(function() {
				$modalInstance.close();
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};


}]);