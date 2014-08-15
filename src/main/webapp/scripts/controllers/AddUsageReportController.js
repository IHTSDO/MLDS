'use strict';

angular.module('MLDS').controller('AddUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'affiliateId',
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, affiliateId) {
	$scope.alerts = [];
	
	$scope.ranges = CommercialUsageService.generateRanges();
	$scope.selectedRange = $scope.ranges[0];
	
	$scope.add = function(range){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.createUsageReport(affiliateId, range.startDate, range.endDate)
			.then(function(result) {
				//FIXME who should do this?
				$location.path('/usage-reports/usage-log/'+result.data.commercialUsageId);
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