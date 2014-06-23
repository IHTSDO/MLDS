'use strict';

angular.module('MLDS').controller('AddInstitutionController', ['$scope', '$modalInstance', 'country', 'usageReport', '$timeout', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, country, usageReport, $timeout, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.usageReport = usageReport;
	$scope.alerts = [];
	$scope.institution = {};
	$scope.institution.startDate = new Date();
	$scope.institution.countryCode = country.isoCode2;
	
	//TODO: AC rename(if needed) and fill in guts to submit new institution
	$scope.add = function(){
		$scope.submitting = true;
		
		CommercialUsageService.addUsageEntry(usageReport, $scope.institution)
			.then(function(result) {
				$modalInstance.dismiss('cancel');
			})
			.catch(function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
				$scope.submitting = false;
			});
	};
	
	//TODO: example of adding alerts/errors to modal to be moved into 'add' function
	$scope.addFail = function(){
		$scope.submitting = true;
		
		$timeout(function() {
			$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
			$scope.submitting = false;
		}, 2000);
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

	$scope.datepickers = {
	    startDate: false,
	    ceaseDate: false
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