'use strict';

mldsApp.controller('UsageReportsTableController', [
		'$scope',
		'UsageReportsService',
		function($scope, UsageReportsService) {
			$scope.usageReportsUtils = UsageReportsService;
		} ]);
