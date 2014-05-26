'use strict';

angular.module('MLDS')
    .controller('UserRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', function ($scope, $log, UserRegistrationService) {
        	
        	$scope.user = {};
        	
        	$scope.reset = function() {
        		$scope.user = {};
        	};
        	
        	$scope.createUser = function() {
        		UserRegistrationService.createUser($scope.user).then(function(response) {
					//Success Handler        			
				});
        	};
        	
        }
    ]);