'use strict';

angular.module('MLDS')
.factory('UserAffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', 'AffiliateService', 'ApplicationUtilsService',
                                    function($http, $rootScope, $log, $q, Session, AffiliateService, ApplicationUtilsService){
	var service = {
		affiliate: null,
		approvedMemberships: [],
		incompleteMemberships: []
	};
	
	var loadUserAffiliate = function loadUserAffiliate() {
		service.affiliate = null;
		service.promise = AffiliateService.myAffiliate();
		service.promise.then(function(resp){
			setAffiliate(resp.data);
		});
	};
	loadUserAffiliate();
	
	$rootScope.$on('event:auth-loginConfirmed', loadUserAffiliate);
	$rootScope.$on('event:auth-loginCancelled', loadUserAffiliate);
	
	var setAffiliate = function setAffiliate(affiliate) {
		service.affiliate = affiliate;
		service.approvedMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isApplicationApproved)
			.pluck('member')
			.value();
		service.incompleteMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isApplicationIncomplete)
			.pluck('member')
			.value();
	};
	
	return service;
}]);
