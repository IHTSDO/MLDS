'use strict';

angular.module('MLDS').controller('ApplicationSummaryModalController', 
		['$scope', '$log', '$modalInstance', 'application', 'audits',
		 function($scope, $log,  $modalInstance, application, audits) {
	
			
	$scope.application = application;
	$scope.audits = audits;
	
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$log.log('audits', audits);
}]);