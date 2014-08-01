'use strict';

angular.module('MLDS')
.factory('UserAffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', 'AffiliateService',
                                    function($http, $rootScope, $log, $q, Session, AffiliateService){
	var service = {
		affiliate: null
	};
	
	var loadUserAffiliate = function loadUserAffiliate() {
		service.affiliate = null;
		service.promise = AffiliateService.myAffiliate();
		service.promise.then(function(resp){
			service.affiliate = resp.data;
		});
	};
	loadUserAffiliate();
	
	$rootScope.$on('event:auth-loginConfirmed', loadUserAffiliate);
	$rootScope.$on('event:auth-loginCancelled', loadUserAffiliate);
	
	return service;
}]);
