'use strict';

mldsApp.factory('UserRegistrationService', ['$http', '$rootScope', '$log', 'Events', function($http, $rootScope, $log, Events){
		return {
			getUsers: function() {
				return $http.get('/app/rest/users');
			},
		
			createUser : function(user) {
				var httpPromise = $http({
					    method: 'POST',
					    url: '/registrations/create',
					    params: user
					})
					.error(function(data) {
						$log.log(data);
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
			
			getApplication: function() {
				return $http.get('/api/application');
			},
			
			createApplication: function createApplication(applicationForm) {
				$log.log('createApplication', applicationForm);
				return $http.post('/api/application/create', applicationForm);
			},
			
			approveApplication: function approveApplication(username) {
				$log.log('approveApplication', username);
				return $http({
					method: 'POST',
					url: 'api/application/approve',
					params: {email: username}
				});
			}
		};
		
	}]);