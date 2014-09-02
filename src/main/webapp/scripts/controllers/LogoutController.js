'use strict';

angular.module('MLDS')
	.controller('LogoutController',
			
		[ '$location', 
		  'AuthenticationSharedService',
		  function($location, AuthenticationSharedService) {
					AuthenticationSharedService.logout();
				}]);
