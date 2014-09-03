'use strict';

angular.module('MLDS').controller('AdminUsageReportsController',
		['$scope', '$log', 'CommercialUsageService', 'UsageReportsService',
    function ($scope, $log, CommercialUsageService,UsageReportsService) {
			$log.log('AdminUsageReportsController');
			
			$scope.usageReportsUtils = UsageReportsService;
			
			$scope.usageReports = [];
			
			$scope.orderByField = 'submitted';
			$scope.reverseSort = false;
			
			function loadUsageReports(){
				CommercialUsageService.getSubmittedUsageReports()
					.then(function(results) {
						$scope.usageReports = results.data;
					});
			}
			
			loadUsageReports();
    }]);

