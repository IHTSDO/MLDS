'use strict';

angular.module('MLDS').controller('LandingRedirectController', 
		['$scope', '$rootScope', 'Session', '$log', '$location', '$route', 'AuthenticationSharedService', 'UserAffiliateService', 'ApplicationUtilsService',
		 function ($scope, $rootScope, Session, $log, $location, $route, AuthenticationSharedService, UserAffiliateService, ApplicationUtilsService) {
			//$log.log('LandingRedirectController starting', $location.path(), $route.current, window.location.hash)

			Session.promise
				.then(function() {
					//$log.log('LandingRedirectController redirecting', $location.path(), $route.current, window.location.hash)
					if (Session.isStaffOrAdmin()) {
						$location.path('/pendingApplications').replace();		
					} else if (Session.isUser()) {
						UserAffiliateService.promise.then(function() {
							if (ApplicationUtilsService.isApplicationWaitingForApplicant(UserAffiliateService.affiliate.application)) {
								$location.path('/affiliateRegistration');
							} else {
								$location.path('/dashboard').replace();
							}
						});
						
					} else {
						$location.path('/landing').replace();
					}
				});
		}]);
