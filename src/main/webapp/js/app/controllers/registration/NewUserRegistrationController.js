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
        		if ($scope.createUserForm.$invalid) {
        			return;
        		}
        		$scope.error = {};
        		
        		$log.log('createUserForm', $scope.createUserForm);
        		$log.log('$scope', $scope);
        		
        		UserRegistrationService.createUser($scope.user).then(function(response) {
        			$location.path('/emailVerification');  			
				});
        	};
        	
        	$scope.$on(Events.registrationError, function(event, data) {
        		$scope.error = data;
        	});
        	
        	
        }
    ]);