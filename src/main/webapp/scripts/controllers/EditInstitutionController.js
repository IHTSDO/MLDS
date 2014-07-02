'use strict';

angular.module('MLDS').controller('EditInstitutionController', ['$scope', '$modalInstance', 'usageReport', 'institution', 'country', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, usageReport, institution, country, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.institution = institution;
	$scope.usageReport = usageReport;
	
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.updateInstitution = function() {
		$log.log('updateInstitution: ', $scope.institution);
		CommercialUsageService.updateUsageEntry($scope.usageReport, $scope.institution)
			.then(function(result) {
				$modalInstance.close(result);		
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
	
	$scope.open = function($event, datepickerName) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.datepickers[datepickerName] = true;
	};

	$scope.dateOptions = {
		formatYear: 'yy',
		startingDay: 1,
		format: 'yyyy/MM/dd'
	};

}]);