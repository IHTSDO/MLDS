'use strict';

angular.module('MLDS').controller('UsageReportsController',
		['$scope', '$location', '$log', '$modal', 'AffiliateService', 'ApplicationUtilsService', 'UsageReportsService', 'PackageUtilsService',
    function ($scope, $location, $log, $modal, AffiliateService, ApplicationUtilsService, UsageReportsService, PackageUtilsService) {
			$scope.affiliates = [];
			
			$scope.usageReportsUtils = UsageReportsService;
			
        	function loadAffiliates() {
        		AffiliateService.myAffiliates()
	        		.then(function(affiliatesResult) {
	        			var someApplicationsWaitingForApplicant = _.some(affiliatesResult.data, function(affiliate) {
	        				return ApplicationUtilsService.isApplicationWaitingForApplicant(affiliate.application);
	        			});
	        			if (someApplicationsWaitingForApplicant) {
	        				$location.path('/affiliateRegistration');
	        				return;
	        			}
	        			$scope.affiliates = affiliatesResult.data;
	
	        		});
        	}

        	loadAffiliates();
        	
    }]);

