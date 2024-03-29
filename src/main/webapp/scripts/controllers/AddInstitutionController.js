'use strict';

angular.module('MLDS').controller('AddInstitutionController', ['$scope', '$modalInstance', 'country', 'usageReport', '$timeout', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, country, usageReport, $timeout, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.usageReport = usageReport;
	$scope.alerts = [];
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.institution = {};
	$scope.institution.startDate = null;
	$scope.institution.country = country;
	
	//TODO: AC rename(if needed) and fill in guts to submit new institution
	$scope.add = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.addUsageEntry(usageReport, $scope.institution)
			.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure [5]: please try again later.'});
				$scope.submitting = false;
			});
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

	$scope.datepickers = {
	    startDate: false,
	    endDate: false
	};
	
	$scope.open = function($event, datepickerName) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.datepickers[datepickerName] = true;
	};

	$scope.dateOptions = {
		formatYear: 'yyyy',
		startingDay: 1,
		format: 'yyyy-MM-dd'
	};

}]);