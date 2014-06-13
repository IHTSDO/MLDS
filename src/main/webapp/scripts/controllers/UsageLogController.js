'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', function($scope, $log){
	$log.log('UsageLogController');
	
	$scope.usageLogForm = {};
	
	$scope.availableCountries = ["Canada", "Denmark", "France", "United Arab Emirates", "United States", "Germany", "China"];
	
	$scope.addCountries = function() {
		$scope.usageLogForm.countries = $scope.addSelectedCountries;
	};
	
	$scope.removeCountries = function() {
		$scope.removeSelectedCountries.forEach(function(country) {
			$log.log('checking to remove:', country);
			var index = $scope.usageLogForm.countries.indexOf(country);
			
			if (index != -1) {
				$log.log('removing', country);
				$scope.usageLogForm.countries.splice(index, 1);
				
				
			}
		});
	};
	
	$scope.submitUsageLog = function submitUsageLog() {
		$log.log('usageFormSubmit', $scope.usageLogForm);
	};
	
}]);