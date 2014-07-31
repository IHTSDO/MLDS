'use strict';

angular.module('MLDS').controller('UsageReportsController',
		['$scope', '$location', '$log', '$modal', 'AffiliateService', 'UserRegistrationService', 'UsageReportsService',
    function ($scope, $location, $log, $modal, AffiliateService, UserRegistrationService, UsageReportsService) {
			$scope.affiliates = [];

        	function loadAffiliates() {
        		AffiliateService.myAffiliates()
	        		.then(function(affiliatesResult) {
	        			var someApplicationsWaitingForApplicant = _.some(affiliatesResult.data, function(affiliate) {
	        				return UserRegistrationService.isApplicationWaitingForApplicant(affiliate.application);
	        			});
	        			if (someApplicationsWaitingForApplicant) {
	        				$location.path('/affiliateRegistration');
	        				return;
	        			}
	        			$scope.affiliates = affiliatesResult.data;
	        			
	        			affiliatesResult.data.forEach(function(affiliate) {
	        				affiliate.commercialUsages.sort(function(a, b) {
	        					if (a.startDate && b.startDate) {
	        						return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
	        					} else if (a.startDate) {
	        						return 1;
	        					} else {
	        						return -1;
	        					}
	        				});
	        			});
	
	        		});
        	}

        	loadAffiliates();
        	
        	$scope.usageReportsUtils = UsageReportsService;
        	
       
    }]);

