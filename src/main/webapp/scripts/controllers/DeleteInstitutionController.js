'use strict';

angular.module('MLDS').controller('DeleteInstitutionController', ['$scope', '$modalInstance', 'usageReport', 'institution', 'country', '$log', 'CommercialUsageService', 
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
	
	$scope.removeInstitution = function() {
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.deleteUsageEntry($scope.usageReport, $scope.institution)
			.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};
}]);