'use strict';

angular.module('MLDS').controller('ViewLicenseController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$log.log('ViewLicenseController');
		$scope.member = member;
		
		MemberService.getMemberLicense(member.key).success(function(data) {
			$log.log(data);
		});
		
    }]);

