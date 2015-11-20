'use strict';

angular.module('MLDS').controller('StaffTopNavController',
		['$scope', 'Session', 
    function ($scope, Session) {
		$scope.homePageUrl = null;
		
		handleWatch();
		
		$scope.$watch('Session.userRoles', handleWatch);

		function handleWatch() {
			if (Session.isMember()) {
				$scope.homePageUrl = "#/ihtsdoReleases";
			} else {
				$scope.homePageUrl = "#/pendingApplications";
			}
		}
    }]);

