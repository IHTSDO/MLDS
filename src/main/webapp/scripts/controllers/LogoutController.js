'use strict';

angular.module('MLDS')
	.controller('LogoutController',
			
		[ '$location', 
		  'AuthenticationSharedService',
          '$templateCache',
		  function($location, AuthenticationSharedService, $templateCache) {
                    $templateCache.removeAll();
					AuthenticationSharedService.logout();
				}]);
