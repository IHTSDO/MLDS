'use strict';

angular.module('MLDS').controller('RetractUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'commercialUsageReport', 'UserAffiliateService', 
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, commercialUsageReport, UserAffiliateService) {
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.commercialUsageReport = commercialUsageReport;
	
	$scope.retract = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.retractUsageReport($scope.commercialUsageReport)
			.then(function(result) {
				UserAffiliateService.refreshAffiliate();
				$modalInstance.close(result);
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

	
	$scope.open = function($event, datepickerName) {
		$event.preventDefault();
		$event.stopPropagation();
	};


}]);