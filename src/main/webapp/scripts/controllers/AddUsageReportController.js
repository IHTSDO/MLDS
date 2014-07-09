'use strict';

angular.module('MLDS').controller('AddUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'licenseeId',
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, licenseeId) {
	$scope.alerts = [];
	
	$scope.ranges = CommercialUsageService.generateRanges();
	$scope.selectedRange = $scope.ranges[0];
	
	$scope.add = function(range){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.createUsageReport(licenseeId, range.startDate, range.endDate)
			.then(function(result) {
				//FIXME who should do this?
				$location.path('/usage-log/'+result.data.commercialUsageId);
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