'use strict';

angular.module('MLDS').controller('AddUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'licenseeId',
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, licenseeId) {
	$scope.alerts = [];
	
	//FIXME generate ranges...
	$scope.ranges = generateRanges();
	
	$scope.selectedRange = $scope.ranges[0];
	
	$scope.add = function(range){
		$scope.submitting = true;
		
		CommercialUsageService.createUsageReport(licenseeId, range.startDate, range.endDate)
			.then(function(result) {
				//FIXME who should do this?
				$location.path('/usage-log/'+result.data.commercialUsageId);
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

	function generateRangeEntry(start, end) {
		return {
			description: ''+start.format('MMM')+' - '+end.format('MMM YYYY'),
			startDate: start.toDate(),
			endDate: end.toDate()
		};
	}
	
	function generateRanges() {
		var ranges = [];
		var date = moment().utc();
		for (var i = 0; i < 6; i++) {
			var isFirstHalfOfYear = date.isBefore(date.clone().month(6).startOf('month'));
			if (isFirstHalfOfYear) {
				date = date.startOf('year');
				var end = date.clone().month(5).endOf('month');
				ranges.push(generateRangeEntry(date, end));
			} else {
				date = date.month(6).startOf('month');
				var end = date.clone().endOf('year');
				ranges.push(generateRangeEntry(date, end));
			}
			date = date.clone().subtract(2, 'months');
		}
		return ranges;
	}
}]);