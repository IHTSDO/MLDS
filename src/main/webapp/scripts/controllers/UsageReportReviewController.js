'use strict';

angular.module('MLDS').controller('UsageReportReviewController',
		['$scope', '$routeParams', '$location', '$log', '$parse', '$window', 'CommercialUsageService', 'AffiliateService', 'UsageReportsService',
    function ($scope, $routeParams, $location, $log, $parse, $window, CommercialUsageService, AffiliateService, UsageReportsService) {
			
		var usageReportId = $routeParams.usageReportId;
		
		$scope.usageReport = {};
		$scope.affiliate = {};
		
		$scope.usageByCountry = {};
		$scope.usageByCountryList = [];
		
		$scope.alerts = [];
		$scope.submitting = false;
		
		$scope.usageReportsUtils = UsageReportsService;

		$scope.goBackToPrevious = function() {
			$window.history.back();	
		};
		
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
		
		function findUsageReport(reportId) {
			CommercialUsageService.getUsageReport(reportId)
				.then(function(result) {
					$scope.usageReport = result.data;
					$scope.affiliate = result.data.affiliate;
					
					// We must download a fully populated affiliate
					AffiliateService.affiliate($scope.affiliate.affiliateId)
						.then(function(result) {
							$scope.affiliate = result.data;
						});
										
					$scope.usageReport.countries.forEach(function(usageCount) {
						var countrySection = lookupUsageByCountryOrCreate(usageCount.country);
						countrySection.count = usageCount;
					});
					$scope.usageReport.entries.forEach(function(usageEntry) {
						var countrySection = lookupUsageByCountryOrCreate(usageEntry.country);
						countrySection.entries.push(usageEntry);
						sortByNameProperty(countrySection.entries, 'name');
					});
					sortByNameProperty($scope.usageByCountryList, 'country.commonName');
				})
				["catch"](function(message) {
					$log.log("find usage report failure: "+message.statusText);
					$location.path('/usageReportsReview');
				});
		}
		
		findUsageReport(usageReportId);
		
		$scope.updateUsageReport = function updateUsageReport(newState) {
			$scope.alerts.splice(0, $scope.alerts.length);
			$scope.submitting = true;

			CommercialUsageService.updateUsageReport($scope.usageReport, newState)
				.then(function(result) {
					$log.log(result);
					$location.path('/usageReportsReview');
					$scope.submitting = false;
				})
				["catch"](function(message) {
					$log.log("failed to update usage report: "+message.statusText);
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
		};
		
		$scope.retractUsageReport = function() {
			UsageReportsService.retractUsageReport($scope.usageReport);
		}
		
    }]);

