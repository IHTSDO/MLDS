'use strict';

angular.module('MLDS')
.factory('MemberService', ['$http', '$rootScope', '$log', '$q', '$window', '$location', 
                           function($http, $rootScope, $log, $q, $window, $location){
	
		var membersListQ = 
			$http.get('/api/members')
				.then(function(d){return d.data;});
		var service = {};

		service.members = [];
		service.membersByKey = {};
		service.ready = membersListQ;
		
		membersListQ.then(function(members){
			// append to members list
			Array.prototype.push.apply(service.members, members);
			
			service.members.sort(function(a, b) {
				var x = a.key.toLowerCase();
			    var y = b.key.toLowerCase();
			    return x < y ? -1 : x > y ? 1 : 0;
			});
			
			// fill membersByKey map
			service.members.map(function(m){
				service.membersByKey[m.key] = m;
			});
			
		});
		
		function updateMemberEntry(updatedMember) {
			// Update collections in place - assuming only minor updates to the member
			service.membersByKey[updatedMember.key] = updatedMember;
			_.each(service.members, function(member, index) {
				if (member.key === updatedMember.key) {
					service.members[index] = updatedMember;
				}
			});
		}
		
		function reloadMembers() {
			$http.get('/api/members')
			.then(function(result){
				_.each(result.data, function(member, index) {
					updateMemberEntry(member);
				});
			});	
		};

		/* Members object has role specific fields only available after login*/
		$rootScope.$on('event:auth-loginConfirmed', reloadMembers);
		
		//FIXME is there a better way to indicate the IHTSDO/international member?
		service.ihtsdoMemberKey = 'IHTSDO';
		service.ihtsdoMember = {key: service.ihtsdoMemberKey};
		
		service.isIhtsdoMember = function isIhtsdoMember(member) {
			return member && member.key === service.ihtsdoMemberKey;
		};
		
		service.isMemberEqual = function isMemberEqual(a, b) {
			return a && b && a.key === b.key;
		};
		
		service.getMemberLicense = function getMemberLicense(memberKey) {
			$window.open('/api/members/' + encodeURIComponent(memberKey) + '/license', '_blank');
		};

		service.getMemberLogoUrl = function getMemberLogoUrl(memberKey, force) {
			return '/api/members/' + encodeURIComponent(memberKey) + '/logo' + (force ? '?_='+Date.now():'');
		};

		service.openMemberLogo = function openMemberLogo(memberKey) {
			$window.open(service.getMemberLogoUrl(memberKey), '_blank');
		};
		
		service.getMemberLandingPage = function getMemberLandingPage(member) {
			var url = $location.absUrl();
			var hashIndex = url.indexOf('#');
			if (hashIndex != -1) {
				url = url.slice(0, hashIndex+1);
				
			}
			url += '/landing/'+member.key;
			return url;
		};

		service.updateMemberLicense = function updateMemberLicense(memberKey, memberLicenseFile, licenseName, licenseVersion) {
			var formData = new FormData();
	        formData.append('file', memberLicenseFile);
	        formData.append('licenseName', licenseName);
	        formData.append('licenseVersion', licenseVersion);
	        var promise = $http.post('/api/members/' + encodeURIComponent(memberKey) + '/license', formData, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        });
	        promise.then(function(result) {
	        	updateMemberEntry(result.data);
	        });
	        return promise;
		};

		service.updateMemberBrand = function updateMemberLicense(memberKey, memberLogoFile, name) {
			var formData = new FormData();
	        formData.append('file', memberLogoFile);
	        formData.append('name', name);
	        var promise = $http.post('/api/members/' + encodeURIComponent(memberKey) + '/brand', formData, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        });
	        promise.then(function(result) {
	        	updateMemberEntry(result.data);
	        });
	        return promise;
		};
		
		service.updateMemberNotifications = function updateMemberNotifications(memberKey, staffNotificationEmail) {
			var member = {}
			member.staffNotificationEmail = staffNotificationEmail;
	        var promise = $http.put('/api/members/' + encodeURIComponent(memberKey) + '/notifications', member);
	        promise.then(function(result) {
	        	updateMemberEntry(result.data);
	        });
	        return promise;
		};

		service.updateMember = function updateMember(member) {
	        var promise = $http.put('/api/members/' + encodeURIComponent(member.key), member);
	        promise.then(function(result) {
	        	updateMemberEntry(result.data);
	        });
	        return promise;
		};

		return service;
		
	}]);