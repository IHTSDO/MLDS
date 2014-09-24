'use strict';

angular.module('MLDS').controller('MainController', 
		['$scope', '$rootScope', 'Session', '$log', 'AuthenticationSharedService',
         function ($scope, $rootScope, Session, $log, AuthenticationSharedService) {
      		// Used to reverse the result of a function in filters
      		$rootScope.not = function(func) {
      		    return function (item) { 
      		        return !func(item); 
      		    };
      		};
      		$rootScope.Session = Session;

          }]);
