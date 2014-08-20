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

	var addIhtsdoMemberIfMissing = function addIhtsdoMemberIfMissing(memberships) {
		if (! _.some(memberships, MemberService.isIhtsdoMember)) {
			memberships.push(MemberService.ihtsdoMember);
		}
	};
	
	var initializeMemberships = function initializeMemberships() {
		service.approvedMemberships = _.chain(service.affiliate.applications)
			.filter(ApplicationUtilsService.isExtensionApplication)
			.filter(ApplicationUtilsService.isApplicationApproved)
			.pluck('member')
			.value();
		service.incompleteMemberships = _.chain(service.affiliate.applications)
			.filter(!ApplicationUtilsService.isPrimaryApplication)
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
	
	// User packages
	service.orderIhtsdo = function(memberReleases) {
		return !(memberReleases.member && MemberService.isIhtsdoMember(memberReleases.member));
	};
	service.orderApprovedMemberships = function(memberReleases) {
		return !(memberReleases.member && _.some(service.approvedMemberships, _.partial(MemberService.isMemberEqual, memberReleases.member)));
	};
	service.orderIncompleteMemberships = function(memberReleases) {
		return !(memberReleases.member && _.some(service.incompleteMemberships, _.partial(MemberService.isMemberEqual, memberReleases.member)));
	};
	service.orderMemberName = function(memberReleases) {
		//FIXME use translated member name rather than key
		return  memberReleases.member && memberReleases.member.key || 'NONE';
	};
	
	service.releasePackageOrderBy = [service.orderIhtsdo, service.orderApprovedMemberships, service.orderIncompleteMemberships, service.orderMemberName];
	

	
	return service;
}]);
