'use strict';

angular.module('MLDS').controller('LicensesController',
		['$scope', '$log', 'MemberService', 'CountryService',
    function ($scope, $log, MemberService, CountryService) {
		$scope.members = MemberService.members;
		$scope.countries = CountryService.countriesByIsoCode2;
    }]);

