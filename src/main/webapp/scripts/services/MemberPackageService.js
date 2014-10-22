'use strict';

angular.module('MLDS')
.factory('MemberPackageService', ['UserAffiliateService', 'MemberService', '$translate', function(UserAffiliateService, MemberService, $translate){
	// User packages
	var orderIhtsdo = function(memberReleases) {
		return !(memberReleases.member && MemberService.isIhtsdoMember(memberReleases.member));
	};
	var orderApprovedMemberships = function(memberReleases) {
		return !(memberReleases.member && _.some(UserAffiliateService.approvedMemberships, _.partial(MemberService.isMemberEqual, memberReleases.member)));
	};
	var orderIncompleteMemberships = function(memberReleases) {
		return !(memberReleases.member && _.some(UserAffiliateService.incompleteMemberships, _.partial(MemberService.isMemberEqual, memberReleases.member)));
	};
	var orderMemberName = function(memberReleases) {
		//FIXME use translated member name rather than key
		return  memberReleases.member 
			&& (memberReleases.member.key && $translate.instant('global.member.'+memberReleases.member.key))
				|| 'NONE';
	};
	
	return {
		orderBy:[orderIhtsdo, orderApprovedMemberships, orderIncompleteMemberships, orderMemberName]
	};
}]);
