'use strict';

angular.module('MLDS')
	.factory('UserRegistrationService', ['$http', '$rootScope', 'Events', function($http, $rootScope, Events){
		return {
			getUsers: function() {
				return $http.get('/registrations');
			},
		
			createUser : function(user) {
				var httpPromise = $http({
					    method: 'POST',
					    url: '/registrations/create',
					    params: user
					})
					.error(function(data) {
						console.log(data);
						$rootScope.$broadcast(Events.registrationError, data);
					});
				
				httpPromise.then(function() {
					$rootScope.$broadcast(Events.newUser);
				});
				
				return httpPromise;
			}
		};
		
	}]);