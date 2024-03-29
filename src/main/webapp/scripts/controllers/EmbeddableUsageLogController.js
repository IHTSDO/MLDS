'use strict';

angular.module('MLDS').controller('EmbeddableUsageLogController',
		['$scope', '$log', '$modal', '$parse', 'CountryService', 'CommercialUsageService', 'Events', 'Session', '$routeParams', '$location', 'UsageReportStateUtils', 'UsageReportsService', 'StandingStateUtils',
        function($scope, $log, $modal, $parse, CountryService, CommercialUsageService, Events, Session, $routeParams, $location, UsageReportStateUtils, UsageReportsService, StandingStateUtils){
	$scope.collapsePanel = {};

	$scope.usageLogForm = {};
	$scope.selectedCountryCodesToAdd = [];
	$scope.selectedCountryCodesToRemove = [];
	$scope.geographicAdding = 0;
	$scope.geographicRemoving = 0;
	$scope.geographicAlerts = [];

	//FIXME retrieve from service?
	$scope.agreementTypeOptions = ['AFFILIATE_NORMAL', 'AFFILIATE_RESEARCH', 'AFFILIATE_PUBLIC_GOOD'];
	$scope.implementationStatusOptions = ['IMPLEMENTED', 'DEVELOPMENT', 'PLANNING'];

	$scope.availableCountries = [];
	$scope.currentCountries = [];

	// Usage Model
	$scope.commercialUsageReport = {};
	$scope.usageByCountry = {};
	$scope.usageByCountryList = [];

	$scope.readOnly = false;
	$scope.commercialType = false;
	$scope.isActiveUsage = true;

	$scope.isEditable = true;

	$scope.homeCountry = null;

	//FIXME apparently not recommended to share controller state through scope
	$scope.canSubmit = $scope.$parent.usageLogCanSubmit;

	loadParentsUsageReport();
	setupNotificions();

	function setupNotificions() {
		$scope.$on(Events.commercialUsageUpdated, onCommercialUsageUpdated);
		$scope.$on(Events.affiliateTypeUpdated, onAffiliateTypeUpdated);
	}

	function onCommercialUsageUpdated() {
		CommercialUsageService.getUsageReport($scope.commercialUsageReport.commercialUsageId)
			.then(function(result) {
				updateFromUsageReport(result.data);
			})
			["catch"](function(message) {
				//FIXME
				$log.error('Failed commercialUsageUpdated');
			});

	}

	function onAffiliateTypeUpdated(event, newType) {
		if (newType !== $scope.commercialUsageReport.type) {
			$scope.commercialUsageReport.type = newType;
			//FIXME needs to be persisted as we re-read from server, however, should we be triggering the save?
			CommercialUsageService.updateUsageReportType($scope.commercialUsageReport)
			["catch"](function(message) {
				//FIXME
				$log.error('Failed to put usage type');
			});
		}
	}

	function addHomeCountryIfNotSelected() {
		if ($scope.homeCountry) {
			if($scope.canAddSelectedCountries([$scope.homeCountry.isoCode2])) {
				$scope.addSelectedCountries([$scope.homeCountry.isoCode2]);
			};
		}
	}

	function loadParentsUsageReport() {
		//FIXME apparently not recommended to share controller state through scope
		$scope.$parent.usageReportReady
			.then(function(usageReport) {
				$scope.commercialUsageReport = usageReport;
				updateFromUsageReport(usageReport);
			})
			["catch"](function(message) {
				//FIXME
				$log.error('Failed to get initial usage log by param');
			});
	}

	function isCountryAlreadyPresent(country) {
		return ($scope.usageByCountry[country.isoCode2]);
	}

	function lookupUsageByCountryOrNull(country) {
		var countryCode = country.isoCode2;
		return $scope.usageByCountry[countryCode];
	}

	function lookupUsageByCountryOrCreate(country) {
		var countryCode = country.isoCode2;
		var countrySection = lookupUsageByCountryOrNull(country);
		if (!countrySection) {
			countrySection = {
					country: country,
					entries: [],
					count: {
						snomedPractices: 0,
						hospitalsStaffingPractices: 0,
						dataCreationPracticesNotPartOfHospital: 0,
						nonPracticeDataCreationSystems: 0,
						deployedDataAnalysisSystems: 0,
						databasesPerDeployment: 0,
						country: country
					}
			};
			$scope.usageByCountry[countryCode] = countrySection;
			$scope.currentCountries.push(country);
			$scope.usageByCountryList.push(countrySection);
		}
		return countrySection;
	};

	function sortByNameProperty(array, expression) {
		var accessor = $parse(expression);
		array.sort(function(a, b) {
			var x = (accessor(a) || '').toLowerCase();
		    var y = (accessor(b) || '').toLowerCase();
		    return x < y ? -1 : x > y ? 1 : 0;
		});
	}

	function updateFromUsageReport(usageReport) {
		$scope.usageByCountry = {};
		$scope.usageByCountryList = [];
		$scope.availableCountries = [];
		$scope.currentCountries = [];
		$scope.commercialUsageReport = usageReport;
		$scope.isActiveUsage = !usageReport.effectiveTo;
		$scope.isEditable = Session.isUser() || Session.isAdmin(); //Only Admin or User can edit
		$scope.readOnly = $scope.isEditable ? !UsageReportStateUtils.isWaitingForApplicant(usageReport.state) : true;
		$scope.commercialType = usageReport.type === 'COMMERCIAL';
		$scope.isAffiliateApplying = Session.isUser() && usageReport.affiliate && StandingStateUtils.isApplying(usageReport.affiliate.standingState) && $scope.$parent.usageReportRegistration;

		var previousHomeCountry = $scope.homeCountry;
		$scope.homeCountry = (usageReport.affiliate.affiliateDetails) ? usageReport.affiliate.affiliateDetails.address.country : $scope.affiliateform.affiliateDetails.address.country;

		usageReport.countries.forEach(function(usageCount) {
			var countrySection = lookupUsageByCountryOrCreate(usageCount.country);
			countrySection.count = usageCount;
		});
		usageReport.entries.forEach(function(usageEntry) {
			var countrySection = lookupUsageByCountryOrCreate(usageEntry.country);
			countrySection.entries.push(usageEntry);
			sortByNameProperty(countrySection.entries, 'name');
		});
		CountryService.countries.forEach(function(country) {
			if (! lookupUsageByCountryOrNull(country)) {
				$scope.availableCountries.push(country);
			}
		});
		sortByNameProperty($scope.availableCountries, 'commonName');
		sortByNameProperty($scope.usageByCountryList, 'country.commonName');
		sortByNameProperty($scope.currentCountries, 'commonName');

		//FIXME some strange timing about registration setting form Country - work out how to remove this workaround
		if (!previousHomeCountry && $scope.homeCountry) {
			addHomeCountryIfNotSelected();
		}
		/*MLDS-983 Issue removing sub-license usage countries other than India from MLDS application*/
        if ($scope.affiliateform.approvalState === "CHANGE_REQUESTED") {
            $scope.readOnly = false;
        }
        /*MLDS-983 Issue removing sub-license usage countries other than India from MLDS application*/
	};


	function countryFromCode(countryCode) {
		return CountryService.countriesByIsoCode2[countryCode];
	}

	$scope.saveUsage = function() {
		//Skip Broadcast for direct edit of context fields to reduce the chance of input value changing under user as they are typing
		//FIXME is there a better way of handling this scenario?
		CommercialUsageService.updateUsageReportContext($scope.commercialUsageReport, {skipBroadcast: true})
			["catch"](function(message) {
				//FIXME
				$log.error('Failed to put usage context');
			});
	};

	//FIXME required simply for preliminary auto-submit directive
	$scope.submit = $scope.saveUsage;

	$scope.canAddSelectedCountries = function(countryCodes) {
		if ($scope.geographicAdding) {
			return false;
		}
		var canAdd = false;
		countryCodes.forEach(function(countryCode) {
			var addCountry = countryFromCode(countryCode);
			if (addCountry && !isCountryAlreadyPresent(addCountry) && !addCountry.excludeUsage) {
				canAdd = true;
			}
		});
		return canAdd;
	};

	$scope.addSelectedCountries = function(countryCodes) {
		$scope.geographicAdding = 0;
		$scope.geographicAlerts.splice(0, $scope.geographicAlerts.length);

		countryCodes.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			if (country && !isCountryAlreadyPresent(country)) {
				$scope.geographicAdding += 1;
				CommercialUsageService.addUsageCount($scope.commercialUsageReport,
						{
						snomedPractices: 0,
						hospitalsStaffingPractices: 0,
						dataCreationPracticesNotPartOfHospital: 0,
						nonPracticeDataCreationSystems: 0,
						deployedDataAnalysisSystems: 0,
						databasesPerDeployment: 0,
						country: country
				}, {skipBroadcast: true})
				.then(function() {
					$scope.geographicAdding = Math.max($scope.geographicAdding - 1, 0);
					if ($scope.geographicAdding === 0) {
						CommercialUsageService.broadcastCommercialUsageUpdate();
					}
				})
				['catch'](function() {
					$scope.geographicAlerts.push({type: 'danger', msg: 'Network request failure [11]: please try again later.'});
					$scope.geographicAdding = Math.max($scope.geographicAdding - 1, 0);
					if ($scope.geographicAdding === 0) {
						CommercialUsageService.broadcastCommercialUsageUpdate();
					}
				});
			}
		});
		countryCodes.splice(0, countryCodes.length);
	};

	$scope.canRemoveSelectedCountries = function(countryCodes) {
		if ($scope.geographicRemoving) {
			return false;
		}
		return countryCodes.length > 0;
	};

	$scope.removeSelectedCountries = function(countryCodes) {
		countryCodes.forEach(function(countryCode){
			var country = countryFromCode(countryCode);
			removeCountry(country);
		});
		countryCodes.splice(0, countryCodes.length);
	};

	function removeCountry(country) {
		$scope.geographicRemoving = 0;
		$scope.geographicAlerts.splice(0, $scope.geographicAlerts.length);

		if (country && isCountryAlreadyPresent(country)) {
			var countrySection = lookupUsageByCountryOrCreate(country);
			$scope.geographicRemoving += 1;
			CommercialUsageService.deleteUsageCount($scope.commercialUsageReport, countrySection.count, {skipBroadcast: true})
			.then(function() {
				$scope.geographicRemoving = Math.max($scope.geographicRemoving - 1, 0);
				if ($scope.geographicRemoving === 0) {
					CommercialUsageService.broadcastCommercialUsageUpdate();
				}
			})
			['catch'](function() {
				$scope.geographicAlerts.push({type: 'danger', msg: 'Network request failure [49]: please try again later.'});
				$scope.geographicRemoving = Math.max($scope.geographicRemoving - 1, 0);
				if ($scope.geographicRemoving === 0) {
					CommercialUsageService.broadcastCommercialUsageUpdate();
				}
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

	$scope.editCountDataAalysisModal = function(count, country) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/editCountDataAnalysisModal.html',
			controller: 'EditCountDataAnalysisController',
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
				},
				hospitalsCount: function() {
					var countrySection = lookupUsageByCountryOrCreate(count.country);
					return countrySection.entries.length;

				},
				practicesCount: function() {
					return count.snomedPractices;

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
					return count.snomedPractices;

				}

			}
		});
	};


	$scope.submitUsageReport = function(form) {
		if (form.$invalid) {
			form.attempted = true;
			return;
		}

		var modalInstance = $modal.open({
			templateUrl: 'views/user/submitUsageReportModal.html',
			controller: 'SubmitUsageReportController',
			size:'lg',
			backdrop: 'static',
			resolve: {
				commercialUsageReport: function() {
					return $scope.commercialUsageReport;
				},
				usageByCountryList: function() {
					return $scope.usageByCountryList;
				}
			}
		});

	};

	$scope.retractUsageReport = function() {
		UsageReportsService.retractUsageReport($scope.commercialUsageReport);
	}

	$scope.institutionDateRangeOutsidePeriod = function(institution) {
		if (institution.startDate && institution.endDate) {
			var date = new Date(institution.endDate);
			return (date < new Date($scope.commercialUsageReport.startDate));
		}
		return false;
	};
}]);
