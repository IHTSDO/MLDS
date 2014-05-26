'use strict';

angular.module('MLDS')
    .controller('UserListController',
        [ '$scope', '$log', 'UserRegistrationService', function ($scope, $log, UserRegistrationService) {
        	
        	$scope.users = [];
        	
        	function getUsers() {
        		var queryPromise =  UserRegistrationService.getUsers();
        		
        		queryPromise.success(function(data) {
        			$scope.users = data;
        		});
        	}

        	getUsers();
        }
    ]);