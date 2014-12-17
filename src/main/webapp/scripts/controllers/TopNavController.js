'use strict';

angular.module('MLDS').controller('TopNavController',
		['$rootScope', '$scope', '$routeParams', '$log', '$timeout', '$location', '$filter', 'Session', 'MemberService',
    function ($rootScope, $scope, $routeParams, $log, $timeout, $location, $filter, Session, MemberService) {
		$scope.memberReady = false;
		$scope.memberName = '';
		$scope.memberLogo = '';
		
		$scope.$watch('Session.member', function(memberKey) {
			if (Session.member) {
				MemberService.ready.then(function() {
					$scope.memberName = Session.member ? $filter('enum')(Session.member.key, 'global.member.') : '';
					$scope.memberLogo = '';
					var member = MemberService.membersByKey[Session.member.key];
					if (member && member.name) {
						$scope.memberName = member.name;
					}
					if (member && member.logo) {
						$scope.memberLogo = MemberService.getMemberLogoUrl(member.key);
					}
					$scope.memberReady = true;
				});
			} else {
				$scope.memberReady = false;
				$scope.memberName = '';
				$scope.memberLogo = '';
			}
		});
    }]);

