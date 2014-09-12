'use strict';

angular.module('MLDS').controller('AdminUsageReportsController',
		['$scope', '$log', 'CommercialUsageService', 'UsageReportsService', 'StandingStateUtils',
    function ($scope, $log, CommercialUsageService,UsageReportsService, StandingStateUtils) {
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
			
			$scope.affiliateDetails = function(usageReport) {
				if (!usageReport || !usageReport.affiliate) {
					return {};
				}
				var affiliate = usageReport.affiliate;
				if (StandingStateUtils.wasApproved(affiliate.standingState)) {
					return affiliate.affiliateDetails;
				} else {
					//TODO would like to find application when not included
					return affiliate.application && affiliate.application.affiliateDetails || {};
				}
			};
    }]);

