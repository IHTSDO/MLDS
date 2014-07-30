'use strict';

angular.module('MLDS').controller('ApplicationSummaryModalController', 
		['$scope', '$log', '$modalInstance', 'application',
		 function($scope, $log,  $modalInstance, application) {
	
			
	$scope.application = application;
	
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
}]);