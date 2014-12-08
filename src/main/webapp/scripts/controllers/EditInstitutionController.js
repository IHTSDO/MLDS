'use strict';

angular.module('MLDS').controller('EditInstitutionController', ['$scope', '$modalInstance', 'usageReport', 'institution', 'country', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, usageReport, institution, country, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.institution = institution;
	$scope.usageReport = usageReport;
	
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.updateInstitution = function() {
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.updateUsageEntry($scope.usageReport, $scope.institution)
			.then(function(result) {
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
	
	
	// DatePicker Configs
	$scope.datepickers = {
	    startDate: false,
	    endDate: false
	};
	
	$scope.dateOptions = {
			formatYear: 'yyyy',
			startingDay: 1,
			format: 'yyyy-MM-dd'
		};

	$scope.open = function($event, datepickerName) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.datepickers[datepickerName] = true;
	};

}]);