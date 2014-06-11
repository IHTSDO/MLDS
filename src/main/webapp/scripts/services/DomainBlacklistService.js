'use strict';

mldsApp.factory('DomainBlacklistService', ['$http', '$log', function($http, $log){
		return {
			getDomainBlacklist: function() {
				return $http.get('/api/domain-blacklist');
			},
		
			addDomain: function addDomain(domainName) {
				$log.log('addDomain', domainName);
				return $http({
					method: 'POST',
					url: 'api/domain-blacklist/create',
					params: {domain: domainName}
				});
			},
			
			removeDomain: function removeDomain(domainName) {
				$log.log('removeDomain', domainName);
				return $http({
					method: 'POST',
					url: 'api/domain-blacklist/remove',
					params: {domain: domainName}
				});
			}
		};
		
	}]);