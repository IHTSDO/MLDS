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
		CommercialUsageService.deleteUsageEntry($scope.usageReport, $scope.institution)
			.then(function(result) {
				$modalInstance.dismiss();
			});
	};
}]);