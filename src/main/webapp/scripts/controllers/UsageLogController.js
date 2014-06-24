'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', 'CommercialUsageService', 'Events', 'Session', '$routeParams', 
                                                 		function($scope, $log, $modal, CountryService, CommercialUsageService, Events, Session, $routeParams){
	$log.log('UsageLogController');
	$log.log($routeParams);
	
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.selectedCountryCodesToAdd = [];
	
	$scope.availableCountries = CountryService.countries;
	
	//FIXME probably should not be retrieving licenseeId from Session
	$scope.licenseeId = Session.login;
	
	// Usage Model
	$scope.commercialUsageReport = {};
	$scope.usageByCountry = {};
	
	
	$scope.$on(Events.commercialUsageUpdated, function() {
		CommercialUsageService.getUsageReport($scope.commercialUsageReport.commercialUsageId)
			.then(function(result) {
				updateFromUsageReport(result.data);
			});
	});
	
	function updateFromUsageReport(usageReport) {
		$scope.commercialUsageReport = usageReport;
		$scope.usageByCountry = {};
		usageReport.entries.forEach(function(usageEntry) {
			var countryCode = usageEntry.country.isoCode2;
			var countrySection = $scope.usageByCountry[countryCode];
			if (!countrySection) {
				countrySection = {
						country: usageEntry.country,
						entries: []
				};
				$scope.usageByCountry[countryCode] = countrySection;
			}
			countrySection.entries.push(usageEntry);
		});
	}
	
	CountryService.ready.then(function() {
		if ($routeParams && $routeParams.usageReportId) {
			CommercialUsageService.getUsageReport($routeParams.usageReportId)
				.then(function(result) {
					updateFromUsageReport(result.data);	
				})
				.catch(function(message) {
					//FIXME
					$log.log('Failed to get initial usage log by param');
				});
		} else {
			CommercialUsageService.createUsageReport($scope.licenseeId, new Date('2014-01-02'), new Date('2014-06-30'))
				.then(function(result) {
					updateFromUsageReport(result.data);	
				})
				.catch(function(message) {
					//FIXME
					$log.log('Failed to get initial usage log');
				});
		}
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