'use strict';

mldsApp.controller('UsageReportsTableController', [
		'$scope',
		'$log',
		'UsageReportsService',
		function($scope, $log, UsageReportsService) {
			$scope.usageReportsUtils = UsageReportsService;
			$scope.showAllColumns = true;
			$scope.showViewAll = false;
			
			$scope.isActiveUsageReport = function(usageReport) {
				return !!usageReport.effectiveTo;
			};
			
			$scope.setShowAllColumns = function(set) {
				$scope.showAllColumns = set;
			};
			$scope.setShowViewAll = function(set) {
				$scope.showViewAll = set;
			};
		} ]);
