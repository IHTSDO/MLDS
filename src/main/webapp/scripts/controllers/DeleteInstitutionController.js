'use strict';

angular.module('MLDS').controller('DeleteInstitutionController', ['$scope', '$modalInstance', 'institution', 'country', '$log', 'CommercialUsageService', 
                                                       	function($scope, $modalInstance, institution, country, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.institution = institution;
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.removeInstitution = function() {
		CommercialUsageService.deleteUsageEntry($scope.institution)
			.then(function(result) {
				$modalInstance.dismiss();
			});
	};
}]);