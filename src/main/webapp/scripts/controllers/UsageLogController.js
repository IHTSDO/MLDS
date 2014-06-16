'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', function($scope, $log){
	$log.log('UsageLogController');
	
	$scope.usageLogForm = {};
	$scope.usageLogForm.countries = [];
	
	$scope.availableCountries = ["Canada", "Denmark", "France", "United Arab Emirates", "United States"];
	
	$scope.addCountries = function() {
		$scope.addSelectedCountries.forEach(function(country){
			if ($scope.usageLogForm.countries.indexOf(country) == -1){
				$scope.usageLogForm.countries.push(country);
			}
		});
	};
	
	$scope.removeCountries = function() {
		$scope.removeSelectedCountries.forEach(function(country) {
			var index = $scope.usageLogForm.countries.indexOf(country);
			
			if (index != -1) {
				$scope.usageLogForm.countries.splice(index, 1);
			}
		});
	};
	
	$scope.submitUsageLog = function submitUsageLog() {
		$log.log('usageFormSubmit', $scope.usageLogForm);
	};
	
}]);