angular.module('MLDS').factory('ServicesUtils', [
    '$window', '$modal','$log', '$http', '$location', '$routeParams',
    function( $window, $modal, $log, $http, $location, $routeParams) {
        return {
            $window: $window,
            $modal: $modal,
            $log: $log,
            $http: $http,
            $location: $location,
            $routeParams: $routeParams
        };
    }
]);
