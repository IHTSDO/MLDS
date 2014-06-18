'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', function($scope, $log, $modal){
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
	
	
	$scope.openAddInstitutionModal = function(country) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/addInstitutionModal.html',
			controller: 'AddInstitutionController',
			size:'lg',
			resolve: {
				country: function() {
					return country;
				}
			}
		});
		
	};
	
}]);