'use strict';

angular.module('MLDS').controller('MemberLandingPageController', 
		['$scope', '$rootScope', 'Session', '$log', '$routeParams', 'MemberService',
         function ($scope, $rootScope, Session, $log, $routeParams, MemberService) {
			var memberKey = $routeParams.memberKey;
			console.log(memberKey);
			MemberService.ready.then(function() {
				var member = MemberService.membersByKey[memberKey];
				
				if (member !== null) {
					// Introduce a new memberLanding variable rather than reuse Session.member as no
					// authority is conferred by hitting this public page
					$rootScope.memberLanding = member;
				}
			});
      }]);