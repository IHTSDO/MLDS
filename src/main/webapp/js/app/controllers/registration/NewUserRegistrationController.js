'use strict';

angular.module('MLDS')
    .controller('NewUserRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', function ($scope, $log, UserRegistrationService) {
        	
        	$scope.user = {};
        	
        	$scope.reset = function() {
        		$scope.user = {};
        	};
        	
        	$scope.createUser = function() {
        		UserRegistrationService.createUser($scope.user).then(function(response) {
        			// FIXME MLDS-02 Extract navigation to service.
					window.location = '#/emailVerification';  			
				});
        	};
        	
        }
    ]);