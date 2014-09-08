'use strict';

angular.module('MLDS').controller('RemoveReleaseModalController', 
		['$scope', '$modalInstance',  '$log', 
       	function($scope, $modalInstance, $log) {
			
	$log.log('RemoveReleaseModalController');
	$scope.alerts = [];
	
	$scope.submit = function(){
		$scope.submitting = true;
		
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};


}]);