'use strict';

//FIXME: Rename usersession and move into /scripts

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', 'AffiliateService', 'Session', 'UserRegistrationService', 'UsageReportsService',
          function ($scope, $log, $location, AffiliateService, Session, UserRegistrationService, UsageReportsService) {
        	
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

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
        	
        	$scope.isApplicationPending = function(application) {
        		return UserRegistrationService.isApplicationPending(application);
        	};
        	
        	$scope.isApplicationWaitingForApplicant = function(application) {
        		return UserRegistrationService.isApplicationWaitingForApplicant(application);
        	};
        	
        }
    ]);