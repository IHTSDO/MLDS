'use strict';

angular.module('MLDS')
	.factory('UserRegistrationService', ['$http', function($http){
		return {
			getUsers: function() {
				return $http.get('/registrations');
			},
		
			createUser : function(user) {
				var httpPromise = $http({
					    method: 'POST',
					    url: '/registrations/create',
					    params: user
					});
				return httpPromise;
			}
		};
		
	}]);