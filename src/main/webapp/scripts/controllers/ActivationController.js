'use strict';

angular.module('MLDS').controller('ActivationController',
		['$rootScope', '$scope', '$routeParams', '$log', 'Activate', '$timeout', '$location', 'Session',
    function ($rootScope, $scope, $routeParams, $log, Activate, $timeout, $location, Session) {
		$rootScope.authenticationError = false;
	    $rootScope.authenticated = false;
	    $rootScope.account = null;
	    Session.invalidate();
	    
        Activate.get({key: $routeParams.key},
            function (value, responseHeaders) {
        		$log.log('ok');
                $scope.error = null;
                $scope.success = 'OK';
                $timeout(function() { $location.path('/dashboard'); }, 2000);
            },
            function (httpResponse) {
            	$log.log('error');
                $scope.success = null;
                $scope.error = "ERROR";
            });
    }]);

