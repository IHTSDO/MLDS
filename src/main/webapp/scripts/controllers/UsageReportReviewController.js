'use strict';

angular.module('MLDS').controller('UsageReportReviewController',
		['$scope', '$routeParams', '$location', '$log', '$window', 'CommercialUsageService',
    function ($scope, $routeParams, $location, $log, $window, CommercialUsageService) {
			
		var usageReportId = $routeParams.usageReportId;
		
		$scope.usageReport = {};
		$scope.affiliate = {};
		
		function findUsageReport(reportId) {
			CommercialUsageService.getUsageReport(reportId)
				.then(function(result) {
					$scope.usageReport = result.data;
					$scope.affiliate = result.data.affiliate;
				})
				["catch"](function(message) {
					$log.log("find usage report failure: "+message.statusText);
					$location.path('/usageReports');
				});
		}
		
		findUsageReport(usageReportId);
		
    }]);

