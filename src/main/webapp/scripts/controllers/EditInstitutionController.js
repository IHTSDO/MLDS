'use strict';

angular.module('MLDS').controller('EditInstitutionController', ['$scope', '$modalInstance', 'institution', 'country', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, institution, country, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.institution = institution;
	$scope.attemptedSubmit = false;
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.updateInstitution = function() {
		$log.log('updateInstitution: ', $scope.institution);
		CommercialUsageService.updateUsageEntry($scope.institution)
			.then(function(result) {
				$modalInstance.dismiss();		
			});
	};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
	
	
	// DatePicker Configs
	$scope.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.opened = true;
	};
	
	$scope.dateOptions = {
		formatYear: 'yy',
		startingDay: 1,
		format: 'yyyy/MM/dd'
	};
}]);