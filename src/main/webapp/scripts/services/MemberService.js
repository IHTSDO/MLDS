'use strict';

angular.module('MLDS')
.factory('MemberService', ['$http', '$log', '$q', function($http, $log, $q){
	
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
			
			$log.log('MemberService', service);
		});
		
		return service;
		
	}]);