'use strict';

angular.module('MLDS').factory('Session', ['USER_ROLES','$q', '$log',
	    function (USER_ROLES, $q, $log) {
			var loadedDefer = $q.defer();
			this.promise = loadedDefer.promise;
			
	        this.create = function (login, firstName, lastName, email, userRoles, member) {
	            this.login = login;
	            this.firstName = firstName;
	            this.lastName = lastName;
	            this.email = email;
	            this.userRoles = userRoles;
	            this.member = member;
	            loadedDefer.resolve(this);
	        };
	        this.invalidate = function () {
	            this.login = null;
	            this.firstName = null;
	            this.lastName = null;
	            this.email = null;
	            this.userRoles = null;
	            this.member = null;
	            loadedDefer.resolve(this);
	        };
	        this.isAdmin = function() {
	        	return this.userRoles && _.contains(this.userRoles, USER_ROLES.admin);
	        };
	        this.isStaffOrAdmin = function() {
	        	return this.userRoles && (_.contains(this.userRoles, USER_ROLES.admin) || _.contains(this.userRoles, USER_ROLES.staff));
	        };
	        this.isUser = function() {
	        	return this.userRoles && _.contains(this.userRoles, USER_ROLES.user);
	        };
	        
	        this.updateUserName = function(firstName, lastName) {
	        	this.firstName = firstName;
	        	this.lastName = lastName;
	        };
	        return this;
	    }]);
