'use strict';

angular.module('MLDS')
    .controller('UserListController',
        [ '$scope', '$log', 'UserRegistrationService', 'Events', function ($scope, $log, UserRegistrationService, Events) {
        	$scope.users = [];
        	
        	function getUsers() {
        		var queryPromise =  UserRegistrationService.getUsers();
        		
        		queryPromise.success(function(data) {
        			$scope.users = data;
        		});
        	}
        	
        	getUsers();
        	
        	$scope.$on(Events.newUser, getUsers);
        }
    ]);