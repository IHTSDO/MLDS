'use strict';

angular.module('MLDS').controller('MemberManagementController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 
		 function ($scope, $log, $modal, MemberService, Session) {
		$scope.members = MemberService.members;
		$scope.canAccess = function(member) {
			return Session.isAdmin || member.key === Session.member.key;
		};
		
		$scope.viewLicence = function (memberKey) {
			MemberService.getMemberLicence(memberKey);
		};
		
		$scope.editLicence = function editLicence(member) {
			var modalInstance = $modal.open({
                templateUrl: 'views/admin/editLicence.html',
                controller: 'EditLicenceController',
                scope: $scope,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                  member: function() {
                  	return angular.copy(member);
                  }
                }
              });
		};

		$scope.viewLogo = function (memberKey) {
			MemberService.openMemberLogo(memberKey);
		};

		$scope.editMember = function editMember(member) {
			var modalInstance = $modal.open({
                templateUrl: 'views/admin/editMember.html',
                controller: 'EditMemberController',
                scope: $scope,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                  member: function() {
                  	return angular.copy(member);
                  }
                }
              });
		};
	}]);

