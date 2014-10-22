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
			service.setAffiliate(resp.data);
		});
	};
	
	service.refreshAffiliate =  loadUserAffiliate;
	loadUserAffiliate();
	
	$rootScope.$on('event:auth-loginConfirmed', loadUserAffiliate);
	$rootScope.$on('event:auth-loginCancelled', loadUserAffiliate);

	var initializeMemberships = function initializeMemberships() {
		var primaryApplicationMembers;
		
		// Including memberships from extension applications only as IHTSDO is always primary application
		service.approvedMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isExtensionApplication)
			.filter(ApplicationUtilsService.isApplicationApproved)
			.pluck('member')
			.value();
		service.incompleteMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isExtensionApplication)
			.filter(ApplicationUtilsService.isApplicationIncomplete)
			.pluck('member')
			.value();

		// Include IHTSDO international membership from primary application
		if (service.affiliate.application) {
			primaryApplicationMembers = [MemberService.ihtsdoMember];
			if (service.affiliate.application.member && !MemberService.isMemberEqual(service.affiliate.application.member, MemberService.ihtsdoMember)) {
				primaryApplicationMembers.push(service.affiliate.application.member);
			}
			if (ApplicationUtilsService.isApplicationApproved(service.affiliate.application)) {
				service.approvedMemberships = service.approvedMemberships.concat(primaryApplicationMembers);
			} else if (ApplicationUtilsService.isApplicationIncomplete(service.affiliate.application)) {
				service.incompleteMemberships = service.incompleteMemberships.concat(primaryApplicationMembers);
			}
		}
	};
	
	service.setAffiliate = function setAffiliate(affiliate) {
		service.affiliate = affiliate;
		if (affiliate && affiliate.applications) {
			initializeMemberships();
		}
	};
	
	var isMemberOf = function isMemberOf(member, memberships) {
		return _.some(memberships, _.partial(MemberService.isMemberEqual, member));
	};
	
	service.isMembershipApproved = function isMembershipApproved(member) {
		return isMemberOf(member, service.approvedMemberships);
	};

	service.isMembershipIncomplete = function isMembershipIncomplete(member) {
		return isMemberOf(member, service.incompleteMemberships);
	};

	service.isMembershipNotStarted = function isMembershipNotStarted(member) {
		return !service.isMembershipApproved(member) && !service.isMembershipIncomplete(member);
	};
	
	return service;
}]);
