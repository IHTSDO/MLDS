'use strict';

angular.module('MLDS').controller('LicensesController',
		['$scope', '$log', 'MemberService',
    function ($scope, $log, MemberService) {
		$scope.members = MemberService.members;
    }]);

