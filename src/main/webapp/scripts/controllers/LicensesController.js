'use strict';

angular.module('MLDS').controller('LicensesController',
		['$scope', '$log', '$modal', 'MemberService', 'Session',
    function ($scope, $log, $modal, MemberService, Session) {
		$scope.members = MemberService.members;
		$scope.canAccess = function(member) {
			return member.key === Session.member.key;
		};
		
		$scope.viewLicense = function viewLicense(member) {
			var modalInstance = $modal.open({
                templateUrl: 'views/admin/viewLicense.html',
                controller: 'ViewLicenseController',
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
		
		$scope.editLicense = function editLicense(member) {
			var modalInstance = $modal.open({
                templateUrl: 'views/admin/editLicense.html',
                controller: 'EditLicenseController',
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

