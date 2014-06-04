'use strict';

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', 'UserSession', function ($scope, $log, $location, UserSession) {
        	if (!UserSession.hasApplied()) {
        		$location.path('/affiliateRegistration');
        	} else if (!UserSession.isApproved()) {
        		$location.path('/pendingRegistration');
        	} else {
        		// setup dashboard?
        	}
        }
    ]);