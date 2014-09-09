'use strict';

mldsApp.controller('LoginController', ['$scope', '$location', 'AuthenticationSharedService',
    function ($scope, $location, AuthenticationSharedService) {
        $scope.rememberMe = true;
    	$scope.submitting = false;
        $scope.login = function () {
        	$scope.submitting = true;
        	console.log('submitting');
            AuthenticationSharedService.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            })['finally'](function() {
            	$scope.submitting = false;
            	console.log('submitting done');
            });
        };
    }]);