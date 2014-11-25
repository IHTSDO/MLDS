'use strict';

angular.module('MLDS').controller('AdminUsageReportsController',
		['$scope', '$log', 'CommercialUsageService', 'UsageReportsService', 'StandingStateUtils', 'Session',
    function ($scope, $log, CommercialUsageService,UsageReportsService, StandingStateUtils, Session) {
			
			$scope.usageReportsUtils = UsageReportsService;
			$scope.isAdmin = Session.isAdmin();
			
			$scope.usageReports = [];
			
			$scope.orderByField = 'submitted';
			$scope.reverseSort = false;
			
			var memberKey = Session.member && Session.member.key || 'NONE';
			$scope.reportSearch = function() {
				return (function(report) {
					if ($scope.isAdmin){
						return true;
					}
					return report.affiliate.homeMember.key === memberKey;
				});
			};
			
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

