'use strict';

angular.module('MLDS')
	.controller('LogoutController',
			
		[ '$location', 
		  'AuthenticationSharedService',
          '$templateCache',
          '$log',
		  function($location, AuthenticationSharedService, $templateCache, $log) {
					AuthenticationSharedService.logout();
				}]);
