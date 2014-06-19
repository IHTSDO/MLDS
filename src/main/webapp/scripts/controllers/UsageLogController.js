'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', function($scope, $log, $modal, CountryService){
	$log.log('UsageLogController');
	
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.usageLogForm.countries = [];
	
	$scope.availableCountries = [];
	
	CountryService.getCountries().then(function(results) {
		$scope.availableCountries = results;
	});
	
	$scope.countryFromCode = function(countryCode) {
		var matchedCountry = null;
		$scope.availableCountries.forEach(function(country) {
			if (country.isoCode2 === countryCode) {
				matchedCountry = country;
			}
		});
		return matchedCountry;
	};
	
	$scope.addCountries = function() {
		$scope.addSelectedCountries.forEach(function(countryCode){
			if ($scope.usageLogForm.countries.indexOf(countryCode) == -1){
				$scope.usageLogForm.countries.push(countryCode);
			}
		});
	};
	
	$scope.removeCountries = function() {
		$scope.removeSelectedCountries.forEach(function(countryCode) {
			var index = $scope.usageLogForm.countries.indexOf(countryCode);
			
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
			backdrop: 'static',
			resolve: {
				country: function() {
					return country;
				}
			}
		});
		
	};
	
}]);