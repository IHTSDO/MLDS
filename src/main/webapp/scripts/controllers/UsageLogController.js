'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', function($scope, $log, $modal, CountryService){
	$log.log('UsageLogController');
	
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.selectedCountryCodesToAdd = [];
	
	$scope.availableCountries = CountryService.countries;
	
	//TODO: AC-replace with real institutions
	$scope.institutions = [{name: 'Hospital ABC', type: 'Hospital', startDate: '2010/01/02'}, 
	                       {name: 'Practice A', type: 'Practice', startDate: '2011/02/03'}];
	
	// Usage Model
	$scope.countries = [];
	
	function countryFromCode(countryCode) {
		return CountryService.countriesByIsoCode2[countryCode];
	}
	
	$scope.canAddSelectedCountries = function() {
		var canAdd = false;
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode) {
			var addCountry = countryFromCode(countryCode);
			if (addCountry && $scope.countries.indexOf(addCountry) == -1) {
				canAdd = true;
			}
		});
		return canAdd;
	};
	
	$scope.addSelectedCountries = function() {
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			if ($scope.countries.indexOf(country) == -1){
				$scope.countries.push(country);
			}
		});
	};
	
	$scope.removeCountry = function(country) {
		var index = $scope.countries.indexOf(country);
			
		if (index != -1) {
			$scope.countries.splice(index, 1);
		}
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
	
	$scope.editInstitution = function(institution, country) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/editInstitutionModal.html',
			controller: 'EditInstitutionController',
			size:'sm',
			backdrop: 'static',
			resolve: {
				institution: function() {
					return institution;
				},
				country: function() {
					return country;
				}
			}
		});
		
	};
	
		
	
}]);