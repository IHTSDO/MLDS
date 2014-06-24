'use strict';

angular.module('MLDS').controller('AddUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'licenseeId',
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, licenseeId) {
	$scope.alerts = [];
	
	//FIXME generate ranges...
	$scope.ranges = [
	     {
	    	 description: 'Jul - Dec 2014',
	    	 startDate: new Date('2014-07-01'),
	    	 endDate: new Date('2014-12-31')
	     },
	     {
	    	 description: 'Jan - Jun 2014',
	    	 startDate: new Date('2014-01-01'),
	    	 endDate: new Date('2014-06-30')
	     },
	     {
	    	 description: 'Jul - Dec 2013',
	    	 startDate: new Date('2013-07-01'),
	    	 endDate: new Date('2013-12-31')
	     },
	     {
	    	 description: 'Jan - Jun 2013',
	    	 startDate: new Date('2013-01-01'),
	    	 endDate: new Date('2013-07-30')
	     }
	];
	$scope.selectedRange = $scope.ranges[0];
	
	$scope.add = function(range){
		$scope.submitting = true;
		
		CommercialUsageService.createUsageReport(licenseeId, range.startDate, range.endDate)
			.then(function(result) {
				//FIXME who should do this?
				$location.path('/usage-log/'+result.commercialUsageId);
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