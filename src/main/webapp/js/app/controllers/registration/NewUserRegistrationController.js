'use strict';

angular.module('MLDS')
    .controller('NewUserRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'Events', '$rootScope', function ($scope, $log, UserRegistrationService, $location, Events, $rootScope) {
        	
        	$scope.user = {};
        	$scope.error = {};
        	
        	$scope.reset = function() {
        		$scope.user = {};
        		$scope.error = {};
        	};
        	
        	$scope.createUser = function() {
        		$scope.error = {};
        		
        		UserRegistrationService.createUser($scope.user).then(function(response) {
        			$location.path('/emailVerification');  			
				});
        	};
        	
        	$scope.$on(Events.registrationError, function(event, data) {
        		$scope.error = data;
        	});
        	
        	
        }
    ]);