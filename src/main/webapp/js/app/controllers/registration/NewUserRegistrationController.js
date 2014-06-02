'use strict';

angular.module('MLDS')
    .controller('NewUserRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', function ($scope, $log, UserRegistrationService, $location) {
        	
        	$scope.user = {};
        	
        	$scope.reset = function() {
        		$scope.user = {};
        	};
        	
        	$scope.createUser = function() {
        		UserRegistrationService.createUser($scope.user).then(function(response) {
        			$location.path('/emailVerification');  			
				});
        	};
        	
        }
    ]);