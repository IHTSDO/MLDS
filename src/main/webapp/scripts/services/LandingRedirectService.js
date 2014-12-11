'use strict';

angular.module('MLDS').service('LandingRedirectService', 
		['$log', '$location', '$route', 'UserAffiliateService', 'ApplicationUtilsService',
         function ($log, $location, $route, UserAffiliateService, ApplicationUtilsService) {
			$log.log('LandingRedirectService starting', $location.path(), $route.current, window.location.hash);

			var service = {}; 
			
			service.redirect = function(Session) {
				Session.promise
				.then(function() {
					$log.log('LandingRedirectService redirecting', $location.path(), $route.current, window.location.hash);
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
			};
			
			return service;
			
          }]);
