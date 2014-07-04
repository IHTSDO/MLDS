'use strict';

angular.module('MLDS').controller('ActivationController',
		['$scope', '$routeParams', 'Activate', '$timeout', '$location', 
    function ($scope, $routeParams, Activate, $timeout, $location) {
        Activate.get({key: $routeParams.key},
            function (value, responseHeaders) {
                $scope.error = null;
                $scope.success = 'OK';
                $timeout(function() { $location.path('/dashboard'); }, 2000);
            },
            function (httpResponse) {
                $scope.success = null;
                $scope.error = "ERROR";
            });
    }]);

