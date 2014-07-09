'use strict';

angular.module('MLDS').controller('SubmitUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'commercialUsageReport', 
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, commercialUsageReport) {
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.commercialUsageReport = commercialUsageReport;
	
	// FIXME MB this should be on the CommercialUsageService??
	$scope.commercialUsageInstitutionsByCountry = 
		_.groupBy(commercialUsageReport.entries, 
				function(entry){ return entry.country.isoCode2});

	$scope.submit = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.submitUsageReport($scope.commercialUsageReport)
			.then(function(result) {
				$location.path('/dashboard');
				$modalInstance.close(result);
			})
			["catch"](function(message) {
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