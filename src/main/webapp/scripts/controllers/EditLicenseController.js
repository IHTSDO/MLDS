'use strict';

angular.module('MLDS').controller('EditLicenseController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$log.log('EditLicenseController');
		$scope.member = member;
    }]);

