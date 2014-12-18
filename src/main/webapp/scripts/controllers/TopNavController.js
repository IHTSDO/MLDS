'use strict';

angular.module('MLDS').controller('TopNavController',
		['$rootScope', '$scope', '$routeParams', '$log', '$timeout', '$location', '$filter', 'Session', 'MemberService',
    function ($rootScope, $scope, $routeParams, $log, $timeout, $location, $filter, Session, MemberService) {
		$scope.memberReady = false;
		$scope.memberName = '';
		$scope.memberLogo = '';
		
		$scope.$watch('Session.member', handleWatch);
		$rootScope.$watch('memberLanding', handleWatch);

		function handleWatch() {
			if (Session.member) {
				updateFromMember(Session.member);
			} else if ($rootScope.memberLanding) {
				updateFromMember($rootScope.memberLanding);
			} else {
				$scope.memberReady = false;
				$scope.memberName = '';
				$scope.memberLogo = '';
			}
	
		}
		
		function updateFromMember(sessionMember) {
			MemberService.ready.then(function() {
				$scope.memberName = sessionMember ? $filter('enum')(sessionMember.key, 'global.member.') : '';
				$scope.memberLogo = '';
				var member = MemberService.membersByKey[sessionMember.key];
				if (member && member.name) {
					$scope.memberName = member.name;
				}
				if (member && member.logo) {
					$scope.memberLogo = MemberService.getMemberLogoUrl(member.key);
				}
				$scope.memberReady = true;
			});

		}
    }]);

