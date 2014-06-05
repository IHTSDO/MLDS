'use strict';

angular.module('MLDS')
    .controller('LogoutController',
        [ '$scope', '$log', '$location', '$window', '$http', function ($scope, $log, $location, $window, $http) {
    		$log.log('logging out');
        	$http.post('/j_spring_security_logout').then(function(){
        		$log.log('logged out');
        		$location.path("/");
        		$window.location.reload(true);
        	});
        }
    ]);
