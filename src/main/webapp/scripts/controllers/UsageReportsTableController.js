'use strict';

mldsApp.controller('UsageReportsTableController', [
		'$scope',
		'$log',
		'UsageReportsService',
		function($scope, $log, UsageReportsService) {
			$scope.usageReportsUtils = UsageReportsService;
			
			$scope.isActiveUsageReport = function(usageReport) {
				return !!usageReport.effectiveTo;
			};
		} ]);
