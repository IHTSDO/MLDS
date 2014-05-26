'use strict';

angular.module('MLDS')
	.factory('UserRegistrationService', ['$http', function($http){
		return {
			getUsers: function() {
				return $http.get('http://localhost:8080/registrations');
			}
		};
		
	}]);