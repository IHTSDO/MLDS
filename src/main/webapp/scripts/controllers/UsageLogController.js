'use strict';

angular.module('MLDS').controller('UsageLogController', ['$scope', '$log', '$modal', 'CountryService', 'CommercialUsageService', 'Events', 'Session', '$routeParams', 
                                                 		function($scope, $log, $modal, CountryService, CommercialUsageService, Events, Session, $routeParams){
	$scope.collapsePanel = {};
	
	$scope.usageLogForm = {};
	$scope.selectedCountryCodesToAdd = [];
	$scope.selectedCountryCodesToRemove = [];
	$scope.geographicAdding = 0;
	$scope.geographicRemoving = 0;
	$scope.geographicAlerts = [];
	
	//FIXME retrieve from service?
	$scope.agreementTypeOptions = ['AFFILIATE_NORMAL', 'AFFILIATE_RESEARCH', 'AFFILIATE_PUBLIC_GOOD'];
	
	$scope.availableCountries = CountryService.countries;
	$scope.currentCountries = [];
	
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
			sortByNameProperty($scope.currentCountries, 'commonName');
		}
		return countrySection;
	};
	
	function sortByNameProperty(array, propertyName) {
		array.sort(function(a, b) {
			var x = (a[propertyName] || '').toLowerCase();
		    var y = (b[propertyName] || '').toLowerCase();
		    return x < y ? -1 : x > y ? 1 : 0;
		});
	}
	
	function updateFromUsageReport(usageReport) {
		$scope.usageByCountry = {};
		$scope.currentCountries = [];
		$scope.commercialUsageReport = usageReport;
		$scope.readOnly = usageReport.approvalState !== 'NOT_SUBMITTED';
		$scope.commercialType = usageReport.type === 'COMMERCIAL';
		usageReport.entries.forEach(function(usageEntry) {
			var countrySection = lookupUsageByCountryOrCreate(usageEntry.country);
			countrySection.entries.push(usageEntry);
			sortByNameProperty(countrySection.entries, 'name');
		});
		usageReport.countries.forEach(function(usageCount) {
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
			$log.log('Missing usage report id');
		}
	});
	
	function countryFromCode(countryCode) {
		return CountryService.countriesByIsoCode2[countryCode];
	}
	
	
	
	$scope.saveUsage = function() {
		//Skip Broadcast for direct edit of context fields to reduce the chance of input value changing under user as they are typing
		//FIXME is there a better way of handling this scenario? 
		CommercialUsageService.updateUsageReportContext($scope.commercialUsageReport, {skipBroadcast: true})
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
		$scope.geographicAdding = 0;
		$scope.geographicAlerts.splice(0, $scope.geographicAlerts.length);
		
		$scope.selectedCountryCodesToAdd.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			if (country && !isCountryAlreadyPresent(country)) {
				$scope.geographicAdding += 1;
				CommercialUsageService.addUsageCount($scope.commercialUsageReport, 
						{
						practices: 0,
						country: country
				})
				.then(function() {
					$scope.geographicAdding = Math.max($scope.geographicAdding - 1, 0);
				})
				['catch'](function() {
					$scope.geographicAlerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					$scope.geographicAdding = Math.max($scope.geographicAdding - 1, 0);
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
		$scope.geographicRemoving = 0;
		$scope.geographicAlerts.splice(0, $scope.geographicAlerts.length);
		
		if (country && isCountryAlreadyPresent(country)) {
			var countrySection = lookupUsageByCountryOrCreate(country);
			$scope.geographicRemoving += 1;
			CommercialUsageService.deleteUsageCount($scope.commercialUsageReport, countrySection.count)
			.then(function() {
				$scope.geographicRemoving = Math.max($scope.geographicRemoving - 1, 0);
			})
			['catch'](function() {
				$scope.geographicAlerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
				$scope.geographicRemoving = Math.max($scope.geographicRemoving - 1, 0);
			});

		}
	}

	$scope.addInstitutionModal = function(country) {
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
	
	$scope.editInstitutionModal = function(institution, country) {
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
	
	$scope.deleteInstitutionModal = function(institution, country) {
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

	$scope.editCountModal = function(count, country) {
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

	$scope.removeCountryModal = function(count) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/removeCountryModal.html',
			controller: 'RemoveCountryController',
			size:'lg',
			backdrop: 'static',
			resolve: {
				count: function() {
					return count;
				},
				usageReport: function() {
					return $scope.commercialUsageReport;
				},
				hospitalsCount: function() {
					var countrySection = lookupUsageByCountryOrCreate(count.country);
					return countrySection.entries.length;
					
				},
				practicesCount: function() {
					return count.practices;
					
				}

			}
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