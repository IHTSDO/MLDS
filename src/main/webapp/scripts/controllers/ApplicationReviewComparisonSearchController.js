'use strict';

mldsApp.controller('ApplicationReviewComparisonSearchController', [
		'$scope',
		'$log',
		'LicenseeService',
		function($scope, $log, LicenseeService) {
			$scope.showComparisonLicensee = false; // start with search box showing
			
			$scope.getLicenseesMatching = function getLicenseesMatching(text) {
				return LicenseeService.licenseesResource.query({q:text}).$promise;
			}
			
			$scope.selectComparisonLicensee = function(licensee) {
				$scope.showComparisonLicensee = true;
				$scope.application = $scope.comparisonLicensee.application;
				$log.log(licensee, $scope.comparisonLicensee);
			}
		} ]);
