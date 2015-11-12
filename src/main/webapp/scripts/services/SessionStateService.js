'use strict';

angular.module('MLDS')
.factory('SessionStateService', ['$rootScope', '$log', 
                                    function($rootScope, $log){

	var service = {};
	
	/* Place to store controller state beyond the lifetime of a controller instance */ 
	service.sessionState = {};
	
	function resetSessionState() {
		service.sessionState = {
			affiliatesFilter: {
				standingStateFilter: 'APPLYING',
				standingStateNotApplying: true
			},
			releaseManagementFilter: {
				showAllMembers: ""
			},
			pendingApplicationsFilter: {
				showAllApplications: null,
				orderByField: null,
				reverseSort: null
			}
		};
	}
	
	/* User session state needs to be reset on login */
	$rootScope.$on('event:auth-loginConfirmed', resetSessionState);
	$rootScope.$on('event:auth-loginCancelled', resetSessionState);
	
	resetSessionState();

	return service;
}]);

