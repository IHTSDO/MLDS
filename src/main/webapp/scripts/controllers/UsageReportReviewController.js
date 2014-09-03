'use strict';

angular.module('MLDS').controller('UsageReportReviewController',
		['$scope', '$routeParams', '$location', '$log', '$parse', '$window', 'CommercialUsageService',
    function ($scope, $routeParams, $location, $log, $parse, $window, CommercialUsageService) {
			
		var usageReportId = $routeParams.usageReportId;
		
		$scope.usageReport = {};
		$scope.affiliate = {};
		
		$scope.usageByCountry = {};
		$scope.usageByCountryList = [];
		
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
							practices: 0,
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
					$location.path('/usageReports');
				});
		}
		
		findUsageReport(usageReportId);
		
    }]);

