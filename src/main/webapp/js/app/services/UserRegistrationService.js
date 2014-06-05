'use strict';

angular.module('MLDS')
	.factory('UserRegistrationService', ['$http', '$rootScope', '$log', 'Events', function($http, $rootScope, $log, Events){
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
			},
			
			getApplications: function() {
				return $http.get('/api/applications');
			},
			
			createApplication: function createApplication(applicationForm) {
				$log.log('createApplication', applicationForm);
				return $http.post('/api/applications/create', applicationForm);
			},
			
			approveApplication: function approveApplication(username) {
				return $http.post('/api/applications/approve', username);
			}
		};
		
	}]);