'use strict';

angular.module('MLDS').controller('ShowMemberBrandingController',
		['$scope', '$log', '$modal', '$filter', '$routeParams', 'MemberService', 'Session',
    function ($scope, $log, $modal, $filter, $routeParams, MemberService, Session) {
			
		var memberKey = $routeParams.memberKey;
		
		MemberService.ready.then(function() {
			var member = MemberService.membersByKey[memberKey];
			
			$scope.member = member;
			
			$scope.memberName = member.name || $filter('enum')(member.key, 'global.member.') || '';
			$scope.memberLogo = (member.logo && MemberService.getMemberLogoUrl(member.key, 'force')) || '';
		});
    }]);

