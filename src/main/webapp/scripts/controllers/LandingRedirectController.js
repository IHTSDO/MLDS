'use strict';

angular.module('MLDS').controller('LandingRedirectController', 
		['$scope', '$rootScope', 'Session', '$log', '$location', '$route', 'AuthenticationSharedService',
         function ($scope, $rootScope, Session, $log, $location, $route, AuthenticationSharedService) {
			$log.log('LandingRedirectController starting', $location.path(), $route.current, window.location.hash)

      		Session.promise
      			.then(function() {
      				$log.log('LandingRedirectController redirecting', $location.path(), $route.current, window.location.hash)
      				if (Session.isStaffOrAdmin()) {
      					$location.path('/pendingApplications').replace();		
      				} else if (Session.isUser()) {
      					$location.path('/dashboard').replace();
      				} else {
      					$location.path('/landing').replace();
      				}
      			});
          }]);
