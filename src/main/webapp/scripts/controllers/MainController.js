'use strict';

angular.module('MLDS').controller('MainController', 
		['$scope', '$rootScope', 'Session', '$log', '$location', 'AuthenticationSharedService',
         function ($scope, $rootScope, Session, $log, $location, AuthenticationSharedService) {
      		// Used to reverse the result of a function in filters
      		$rootScope.not = function(func) {
      		    return function (item) { 
      		        return !func(item); 
      		    };
      		};
      		$rootScope.Session = Session;

      		Session.promise
      			.then(function() {
      				if (Session.isStaffOrAdmin()) {
      					$location.path('/pendingApplications').replace();		
      				} else if (Session.isUser()) {
      					$location.path('/dashboard').replace();
      				} else {
      					$location.path('/landing').replace();
      				}
      			});
          }]);
