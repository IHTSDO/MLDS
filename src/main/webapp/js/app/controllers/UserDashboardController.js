'use strict';

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', function ($scope, $log, $location) {
        	var isApplied = false;
        	var isApproved = false;
        	if (!isApplied) {
        		$location.path('/affiliateRegistration');
        		
        	} else if (!isApproved) {
        		// redirect to pending approval
        		$location.path('/pendingRegistration');
        	}
        }
    ]);