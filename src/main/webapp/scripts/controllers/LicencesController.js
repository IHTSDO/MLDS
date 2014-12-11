'use strict';

angular.module('MLDS').controller('LicencesController',
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
                  	//FIXME not sure about copy - needed to support modal cancel or network failure
                  	return angular.copy(member);
                  }
                }
              });
		};
    }]);

