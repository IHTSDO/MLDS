'use strict';

angular.module('MLDS')
.factory('MemberService', ['$http', '$log', '$q', '$window', function($http, $log, $q, $window){
	
		var membersListQ = 
			$http.get('/app/rest/members')
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
			$window.open('/app/rest/members/' + encodeURIComponent(memberKey) + '/license', '_blank');
		};

		service.getMemberLogoUrl = function getMemberLogoUrl(memberKey) {
			return '/app/rest/members/' + encodeURIComponent(memberKey) + '/logo';
		};

		service.openMemberLogo = function openMemberLogo(memberKey) {
			$window.open(service.getMemberLogoUrl(memberKey), '_blank');
		};

		service.updateMemberLicense = function updateMemberLicense(memberKey, memberLicenseFile, licenseName, licenseVersion) {
			var formData = new FormData();
	        formData.append('file', memberLicenseFile);
	        formData.append('licenseName', licenseName);
	        formData.append('licenseVersion', licenseVersion);
	        var promise = $http.post('/app/rest/members/' + encodeURIComponent(memberKey) + '/license', formData, {
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
	        var promise = $http.post('/app/rest/members/' + encodeURIComponent(memberKey) + '/brand', formData, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        });
	        promise.then(function(result) {
	        	updateMemberEntry(result.data);
	        });
	        return promise;
		};

		return service;
		
	}]);