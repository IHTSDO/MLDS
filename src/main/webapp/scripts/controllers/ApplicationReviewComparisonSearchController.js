'use strict';

mldsApp.controller('ApplicationReviewComparisonSearchController', [
		'$scope',
		'$log',
		'AffiliateService',
		function($scope, $log, AffiliateService) {
			$scope.showComparisonAffiliate = false; // start with search box showing
			
			$scope.getAffiliatesMatching = function getAffiliatesMatching(text) {
				return AffiliateService.affiliatesResource.query({q:text}).$promise;
			};
			
			$scope.selectComparisonAffiliate = function(affiliate) {
				$scope.showComparisonAffiliate = true;
				$scope.application = $scope.comparisonAffiliate.application;
				$scope.standingState = $scope.comparisonAffiliate.standingState;
				$log.log(affiliate, $scope.comparisonAffiliate);
			};
		} ]);
