'use strict';

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', function ($scope, $log, $location) {
        	var isApproved = false;
        	if (!isApproved) {
        		// redirect to pending approval
        		$location.path('/pendingRegistration');
        	}
        }
    ]);