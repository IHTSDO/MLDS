angular.module('MLDS').factory('ServicesUtils', [
    '$window', '$modal','$log',
    function( $window, $modal, $log) {
        return {
            $window: $window,
            $modal: $modal,
            $log: $log
        };
    }
]);
