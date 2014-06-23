'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', 'CommercialUsageService', 'Events', 
                                                 		function($scope, $log, $modal, CountryService, CommercialUsageService, Events){
	$log.log('UsageLogController');
	
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.selectedCountryCodesToAdd = [];
	
	$scope.availableCountries = CountryService.countries;
	
	$scope.commercialUsageReport = {};
		
	// Usage Model
	$scope.usageByCountry = {};
	
	
	$scope.$on(Events.commercialUsageUpdated, function() {
		CommercialUsageService.getUsageReport($scope.commercialUsageReport.commercialUsageId)
			.then(function(replacementUsageReport) {
				updateFromUsageReport(replacementUsageReport);
			});
	});
	
	function updateFromUsageReport(usageReport) {
		$scope.commercialUsageReport = usageReport;
		$scope.usageByCountry = {};
		usageReport.usage.forEach(function(usageEntry) {
			var countrySection = $scope.usageByCountry[usageEntry.countryCode];
			if (!countrySection) {
				countrySection = {
						country: countryFromCode(usageEntry.countryCode),
						usage: []
				};
				$scope.usageByCountry[usageEntry.countryCode] = countrySection;
			}
			countrySection.usage.push(usageEntry);
		});
	}
	
	CommercialUsageService.getUsageReports()
		.then(function(usageReports) {
			CommercialUsageService.createUsageReport().then(function(usageReport) {
				updateFromUsageReport(usageReport);	
				
			});
			/*
			updateFromUsageReport(usageReports[0]);
			 */
		})
		.catch(function(message) {
			//FIXME
			$log.log('Failed to get initial usage log');
		});
	
	
	function countryFromCode(countryCode) {
		return CountryService.countriesByIsoCode2[countryCode];
	}
	
	$scope.canAddSelectedCountries = function() {
		var canAdd = false;
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode) {
			var addCountry = countryFromCode(countryCode);
			if (addCountry && !$scope.usageByCountry[countryCode]) {
				canAdd = true;
			}
		});
		return canAdd;
	};
	
	$scope.addSelectedCountries = function() {
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			if (!$scope.usageByCountry[countryCode]){
				$scope.usageByCountry[countryCode] = {
						country: country,
						usage: []
				};
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
				},
				usageReport: function() {
					return $scope.commercialUsageReport;
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
				},
				usageReport: function() {
					return $scope.commercialUsageReport;
				}
			}
		});
		
	};
	
	$scope.deleteInstitution = function(institution, country) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/deleteInstitutionModal.html',
			controller: 'DeleteInstitutionController',
			size:'sm',
			backdrop: 'static',
			resolve: {
				institution: function() {
					return institution;
				},
				country: function() {
					return country;
				},
				usageReport: function() {
					return $scope.commercialUsageReport;
				}
			}
		});
		
	};
	
		
	
}]);