'use strict';

angular.module('MLDS').controller('RetractUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'commercialUsageReport', 
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, commercialUsageReport) {
	$scope.alerts = [];
	
	$scope.commercialUsageReport = commercialUsageReport;
	
	$scope.retract = function(){
		$scope.submitting = true;
		
		CommercialUsageService.retractUsageReport($scope.commercialUsageReport)
			.then(function(result) {
				$modalInstance.dismiss('cancel');
			})
			.catch(function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
				$scope.submitting = false;
			});
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

	
	$scope.open = function($event, datepickerName) {
		$event.preventDefault();
		$event.stopPropagation();
	};


}]);