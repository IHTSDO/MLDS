'use strict';

angular.module('MLDS')
    .controller('UserListController',
        [ '$scope', '$log', 'UserRegistrationService', function ($scope, $log, UserRegistrationService) {
        	
        	$scope.users = [];
        	$scope.user = {};
        	
        	function getUsers() {
        		var queryPromise =  UserRegistrationService.getUsers();
        		
        		queryPromise.success(function(data) {
        			$scope.users = data;
        		});
        	}
        	
        	$scope.reset = function() {
        		$scope.user = {};
        	};
        	
        	$scope.createUser = function() {
        		UserRegistrationService.createUser($scope.user).then(function(response) {
					//Success Handler
        			getUsers();
				});
        	};
        	
        	getUsers();
        }
    ]);