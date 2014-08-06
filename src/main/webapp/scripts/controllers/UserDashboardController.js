'use strict';

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', 'AffiliateService', 'Session', 'ApplicationUtilsService', 'UsageReportsService', 'UserAffiliateService',
          function ($scope, $log, $location, AffiliateService, Session, ApplicationUtilsService, UsageReportsService, UserAffiliateService) {
        	
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

        	$scope.affiliate = UserAffiliateService.affiliate;

        	UserAffiliateService.promise.then(function() {
        		if (ApplicationUtilsService.isApplicationWaitingForApplicant($scope.affiliate.application)) {
        			$location.path('/affiliateRegistration');
        			return;
        		}
        		
        		$scope.affiliate.commercialUsages.sort(function(a, b) {
        			if (a.startDate && b.startDate) {
        				return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
        			} else if (a.startDate) {
        				return 1;
        			} else {
        				return -1;
        			}
        		});
        	});
        	
        	
        	$scope.usageReportsUtils = UsageReportsService;
        	
        	$scope.isApplicationPending = function(application) {
        		return ApplicationUtilsService.isApplicationPending(application);
        	};
        	
        	$scope.isApplicationWaitingForApplicant = function(application) {
        		return ApplicationUtilsService.isApplicationWaitingForApplicant(application);
        	};
        	
        	$scope.isApplicationApproved = function(application) {
        		return application.approvalState === 'APPROVED';
        	};
        	
        }
    ]);
