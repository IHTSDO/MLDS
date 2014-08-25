'use strict';

angular.module('MLDS').controller('UsageReportsController',
		['$scope', '$location', '$log', '$modal', 'AffiliateService', 'ApplicationUtilsService', 'UsageReportsService',
    function ($scope, $location, $log, $modal, AffiliateService, ApplicationUtilsService, UsageReportsService) {
			$scope.affiliates = [];

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

