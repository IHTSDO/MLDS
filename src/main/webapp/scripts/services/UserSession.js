'use strict';
//FIXME: JH-Rename to 'RegistrationState' or something better
angular.module('MLDS')
	.factory('UserSession', ['$http', '$rootScope', 'Events', '$window', function($http, $rootScope, Events, $window){
		// MB - just using shell-provided json data for now.  See FirstPageController.java
		// window.mlds.userRegistration
		
		var service = {};
		
		service.create = function create(emailVerified, applicationMade, applicationApproved) {
			service.emailVerified = emailVerified;
			service.applicationMade = applicationMade;
			service.applicationApproved = applicationApproved;
		};
		
		service.hasVerifiedEmail = function() {
			return service.emailVerified;
		};
		
		service.hasApplied = function hasApplied() {
			return service.applicationMade;
		};
		service.isApproved = function isApproved() {
			return service.applicationApproved;
		};
		
		service.updateSession = function updateSession() {
			service.applicationMade = true;
		};
		
		service.reapplied = function reapplied() {
			service.applicationMade = false;
		};
		
		return service;
	}]);
