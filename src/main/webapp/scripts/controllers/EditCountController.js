'use strict';

angular.module('MLDS').controller('EditCountController', ['$scope', '$modalInstance', 'usageReport', 'count', 'country', '$log', 'CommercialUsageService',
                                                       	function($scope, $modalInstance, usageReport, count, country, $log, CommercialUsageService) {
	$scope.country = country;
	$log.log('count', count);
	$scope.count = count;
	$scope.usageReport = usageReport;
	
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.updateCount = function() {
		$scope.submitAttempted = true;
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		
		CommercialUsageService.updateUsageCount($scope.usageReport, $scope.count)
			.then(function(result) {
				$modalInstance.close(result);		
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure [16]: please try again later.'});
				$scope.submitting = false;
			});
	};

	$scope.closeAlert = function(index) {
		$scope.alerts.splice(index, 1);
	};
}]);