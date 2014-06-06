'use strict';

angular.module('MLDS')
	.factory('UserSession', ['$http', '$rootScope', 'Events', '$window', function($http, $rootScope, Events, $window){
		// MB - just using shell-provided json data for now.  See FirstPageController.java
		// window.mlds.userRegistration
		
		var service = {
		};
		
		service.hasApplied = function hasApplied() {
			return $window.mlds.userInfo.hasApplied;
		};
		service.isApproved = function isApproved() {
			return $window.mlds.userInfo.approved;
		};
		
		service.updateSession = function updateSession() {
			$window.mlds.userInfo.hasApplied = true;
		};
		
		return service;
	}]);
