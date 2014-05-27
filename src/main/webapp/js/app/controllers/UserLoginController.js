'use strict';

angular.module('MLDS')
    .controller('UserLoginController',
        [ '$scope', '$log', '$location', function ($scope, $log, $location) {
        	$scope.loginForm = {};
        	
        	$scope.login = function() {
        		//FIXME: AC-looking for test user to bypass to show registration-flow screen
        		if ($scope.loginForm.username === 'test') {
        			window.location = '#/registration-flow';
        		}
        	};
        }
    ]);