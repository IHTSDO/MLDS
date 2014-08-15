'use strict';

angular.module('MLDS').controller('EditLicenseController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$log.log('EditLicenseController');
		$scope.member = member;
		
		
		$scope.memberLicenseForm = {};
		
		$scope.memberLicenseForm.submit = function submit() {
			$log.log('submit', $scope.memberLicenseForm);
			
			MemberService.updateMemberLicense(member.key, $scope.memberLicenseForm).success(function(data) {
				$log.log('updateMemberLicense', data);
			});
		};
    }]);

