'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', function($scope, $log, $modal, CountryService){
	$log.log('UsageLogController');
	
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.addSelectedCountries = [];
	
	$scope.availableCountries = CountryService.countries;
	
	// Usage Model
	$scope.countries = [];
	
	$scope.countryFromCode = function(countryCode) {
		return CountryService.countriesByIsoCode2[countryCode];
	};
	
	$scope.canAddCountries = function() {
		if ($scope.addSelectedCountries.length > 0) {
			var addCountry = $scope.countryFromCode($scope.addSelectedCountries[0]);
			if (addCountry && $scope.countries.indexOf(addCountry) == -1) {
				return true;
			}
		}
		return false;
	};
	
	$scope.addCountries = function() {
		$scope.addSelectedCountries.forEach(function(countryCode){
			var country = $scope.countryFromCode(countryCode);
			if ($scope.countries.indexOf(country) == -1){
				$scope.countries.push(country);
			}
		});
	};
	
	$scope.removeCountries = function() {
		$scope.removeSelectedCountries.forEach(function(countryCode) {
			var country = $scope.countryFromCode(countryCode);
			var index = $scope.countries.indexOf(country);
			
			if (index != -1) {
				$scope.countries.splice(index, 1);
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