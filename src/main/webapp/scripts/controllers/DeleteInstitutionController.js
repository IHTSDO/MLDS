'use strict';

angular.module('MLDS').controller('DeleteInstitutionController', ['$scope', '$modalInstance', 'usageReport', 'institution', 'country', '$log', 'CommercialUsageService', 
                                                       	function($scope, $modalInstance, usageReport, institution, country, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.institution = institution;
	$scope.usageReport = usageReport;
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.removeInstitution = function() {
		$scope.submitting = true;
		
		CommercialUsageService.deleteUsageEntry($scope.usageReport, $scope.institution)
			.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
				$scope.submitting = false;
			});
	};
}]);