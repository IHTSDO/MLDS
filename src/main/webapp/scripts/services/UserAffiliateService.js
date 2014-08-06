'use strict';

angular.module('MLDS')
.factory('UserAffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', 'AffiliateService', 'ApplicationUtilsService', 'MemberService',
                                    function($http, $rootScope, $log, $q, Session, AffiliateService, ApplicationUtilsService, MemberService){
	var service = {
		affiliate: null,
		approvedMemberships: [],
		incompleteMemberships: []
	};
	
	var loadUserAffiliate = function loadUserAffiliate() {
		service.affiliate = {};
		service.approvedMemberships = [];
		service.incompleteMemberships = [];
		service.promise = AffiliateService.myAffiliate();
		service.promise.then(function(resp){
			setAffiliate(resp.data);
		});
	};
	
	service.refreshAffiliate =  loadUserAffiliate;
	loadUserAffiliate();
	
	$rootScope.$on('event:auth-loginConfirmed', loadUserAffiliate);
	$rootScope.$on('event:auth-loginCancelled', loadUserAffiliate);

	var addIhtsdoMemberIfMissing = function addIhtsdoMemberIfMissing(memberships) {
		if (! _.some(memberships, MemberService.isIhtsdoMember)) {
			memberships.push(MemberService.ihtsdoMember);
		}
	};
	
	var initializeMemberships = function initializeMemberships() {
		service.approvedMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isApplicationApproved)
			.pluck('member')
			.value();
		service.incompleteMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isApplicationIncomplete)
			.pluck('member')
			.value();
		
		// Include IHTSDO international membership if the application was for a member country
		if (service.approvedMemberships.length > 0) {
			addIhtsdoMemberIfMissing(service.approvedMemberships);
		} else if (service.incompleteMemberships.length > 0) {
			addIhtsdoMemberIfMissing(service.incompleteMemberships);
		}
	};
	
	var setAffiliate = function setAffiliate(affiliate) {
		service.affiliate = affiliate;
		if (affiliate && affiliate.applications) {
			initializeMemberships();
		}
	};
	
	return service;
}]);
