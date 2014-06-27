'use strict';

angular.module('MLDS').controller('EditCountController', ['$scope', '$modalInstance', 'usageReport', 'count', 'country', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, usageReport, count, country, $log, CommercialUsageService) {
	$scope.country = country;
	$scope.count = count;
	$scope.usageReport = usageReport;
	
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.updateCount = function() {
		$log.log('updateCount: ', $scope.count);
		//FIXME should this id knowledge be in the service instead - or already clarified before the modal is called
		if (count.commercialUsageCountId) {
			CommercialUsageService.updateUsageCount($scope.usageReport, $scope.count)
				.then(function(result) {
					$modalInstance.dismiss();		
				});
		} else {
			CommercialUsageService.addUsageCount($scope.usageReport, $scope.count)
			.then(function(result) {
				$modalInstance.dismiss();		
			});
		}
	};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
	
	
}]);