'use strict';

angular.module('MLDS').controller('SubmitUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'commercialUsageReport', 
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, commercialUsageReport) {
	$scope.alerts = [];
	
	$scope.commercialUsageReport = commercialUsageReport;
	
	$scope.submit = function(){
		$scope.submitting = true;
		
		CommercialUsageService.submitUsageReport($scope.commercialUsageReport)
			.then(function(result) {
				//FIXME who should do this?
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