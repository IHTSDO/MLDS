'use strict';

//FIXME: Rename usersession and move into /scripts

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', 'UserSession', function ($scope, $log, $location, UserSession) {
        	
        	//FIXME: AC Seems to break when user refreshes page
        	if (!UserSession.hasApplied()) {
        		$location.path('/affiliateRegistration');
        	} else if (!UserSession.isApproved()) {
        		$location.path('/pendingRegistration');
        	} else {
        		// setup dashboard?
        	}
        }
    ]);