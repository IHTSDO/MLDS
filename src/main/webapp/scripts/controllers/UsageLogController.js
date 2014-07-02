'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', 'CommercialUsageService', 'Events', 'Session', '$routeParams', 
                                                 		function($scope, $log, $modal, CountryService, CommercialUsageService, Events, Session, $routeParams){
	$log.log('UsageLogController');
	$log.log($routeParams);
	
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.selectedCountryCodesToAdd = [];
	$scope.selectedCountryCodesToRemove = [];
	
	//FIXME retrieve from service?
	$scope.agreementTypeOptions = ['AFFILIATE_NORMAL', 'AFFILIATE_RESEARCH', 'AFFILIATE_PUBLIC_GOOD'];
	
	$scope.availableCountries = CountryService.countries;
	$scope.currentCountries = [];
	
	//FIXME probably should not be retrieving licenseeId from Session
	$scope.licenseeId = Session.login;
	
	// Usage Model
	$scope.commercialUsageReport = {};
	$scope.usageByCountry = {};
	
	$scope.readOnly = false;
	$scope.commercialType = false;
	
	
	$scope.$on(Events.commercialUsageUpdated, function() {
		CommercialUsageService.getUsageReport($scope.commercialUsageReport.commercialUsageId)
			.then(function(result) {
				updateFromUsageReport(result.data);
			});
	});
	
	function isCountryAlreadyPresent(country) {
		return ($scope.usageByCountry[country.isoCode2]);
	} 
	
	function lookupUsageByCountryOrCreate(country) {
		var countryCode = country.isoCode2;
		var countrySection = $scope.usageByCountry[countryCode];
		if (!countrySection) {
			countrySection = {
					country: country,
					entries: [],
					count: {
						practices: 0,
						country: country
					}
			};
			$scope.usageByCountry[countryCode] = countrySection;
			$scope.currentCountries.push(country);
		}
		return countrySection;
	};
	
	function updateFromUsageReport(usageReport) {
		$scope.usageByCountry = {};
		$scope.currentCountries = [];
		$scope.commercialUsageReport = usageReport;
		$scope.readOnly = usageReport.approvalState !== 'NOT_SUBMITTED';
		$scope.commercialType = usageReport.type === 'COMMERCIAL';
		usageReport.entries.forEach(function(usageEntry) {
			var countrySection = lookupUsageByCountryOrCreate(usageEntry.country);
			countrySection.entries.push(usageEntry);
		});
		usageReport.counts.forEach(function(usageCount) {
			var countrySection = lookupUsageByCountryOrCreate(usageCount.country);
			countrySection.count = usageCount;
		});
	};
	
	CountryService.ready.then(function() {
		if ($routeParams && $routeParams.usageReportId) {
			CommercialUsageService.getUsageReport($routeParams.usageReportId)
				.then(function(result) {
					updateFromUsageReport(result.data);	
				})
				["catch"](function(message) {
					//FIXME
					$log.log('Failed to get initial usage log by param');
				});
		} else {
			CommercialUsageService.createUsageReport($scope.licenseeId, new Date('2014-01-02'), new Date('2014-06-30'))
				.then(function(result) {
					updateFromUsageReport(result.data);	
				})
				["catch"](function(message) {
					//FIXME
					$log.log('Failed to get initial usage log');
				});
		}
	});
	
	function countryFromCode(countryCode) {
		return CountryService.countriesByIsoCode2[countryCode];
	}
	
	
	
	$scope.saveUsage = function() {
		CommercialUsageService.updateUsageReportContext($scope.commercialUsageReport)
		["catch"](function(message) {
			//FIXME
			$log.log('Failed to put usage context');
		});
	};
	
	//FIXME required simply for preliminary auto-submit directive
	$scope.submit = $scope.saveUsage;
	
	$scope.canAddSelectedCountries = function() {
		var canAdd = false;
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode) {
			var addCountry = countryFromCode(countryCode);
			if (addCountry && !isCountryAlreadyPresent(addCountry)) {
				canAdd = true;
			}
		});
		return canAdd;
	};
	
	$scope.addSelectedCountries = function() {
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			if (country && !isCountryAlreadyPresent(country)) {
				CommercialUsageService.addUsageCount($scope.commercialUsageReport, 
						{
						practices: 0,
						country: country
				});
			}
		});
	};

	$scope.canRemoveSelectedCountries = function() {
		return $scope.selectedCountryCodesToRemove.length > 0;
	};

	$scope.removeSelectedCountries = function() {
		$scope.selectedCountryCodesToRemove.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			removeCountry(country);
		});
		$scope.selectedCountryCodesToRemove.splice(0, $scope.selectedCountryCodesToRemove.length);
	};

	function removeCountry(country) {
		if (country && isCountryAlreadyPresent(country)) {
			var countrySection = lookupUsageByCountryOrCreate(country);
			//FIXME this will possible trigger multiple reloads...
			countrySection.entries.forEach(function(entry) {
				CommercialUsageService.deleteUsageEntry($scope.commercialUsageReport, entry);
			});
			CommercialUsageService.deleteUsageCount($scope.commercialUsageReport, countrySection.count); 
		}
	}

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
			size:'lg',
			backdrop: 'static',
			resolve: {
				institution: function() {
					return angular.copy(institution);
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

	$scope.editCount = function(count, country) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/editCountModal.html',
			controller: 'EditCountController',
			backdrop: 'static',
			resolve: {
				count: function() {
					return angular.copy(count);
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

	$scope.openRemoveCountryModal = function(country) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/removeCountryModal.html',
			controller: 'RemoveCountryController',
			size:'lg',
			backdrop: 'static',
			resolve: {
				country: function() {
					return country;
				},
				hospitalsCount: function() {
					var countrySection = lookupUsageByCountryOrCreate(country);
					return countrySection.entries.length;
					
				},
				practicesCount: function() {
					var countrySection = lookupUsageByCountryOrCreate(country);
					return countrySection.count.practices;
					
				}

			}
		});
		modalInstance.result.then(function(result) {
			removeCountry(country);
		});
	};

		
	$scope.submitUsageReport = function() {
		if ($scope.usageForm.$invalid) {
			$scope.usageForm.attempted = true;
			return;
		}

		var modalInstance = $modal.open({
			templateUrl: 'views/user/submitUsageReportModal.html',
			controller: 'SubmitUsageReportController',
			size:'sm',
			backdrop: 'static',
			resolve: {
				commercialUsageReport: function() {
					return $scope.commercialUsageReport;
				}
			}
		});
		
	};

	$scope.retractUsageReport = function() {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/retractUsageReportModal.html',
			controller: 'RetractUsageReportController',
			size:'sm',
			backdrop: 'static',
			resolve: {
				commercialUsageReport: function() {
					return $scope.commercialUsageReport;
				}
			}
		});
		
	};
	

}]);