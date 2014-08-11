'use strict';

mldsApp.controller('ApplicationReviewComparisonSearchController', [
		'$scope',
		'$log',
		'AffiliateService',
		'UserAffiliateService',
		function($scope, $log, AffiliateService, UserAffiliateService) {
			$scope.showComparisonAffiliate = false; // start with search box showing
			
			$scope.getAffiliatesMatching = UserAffiliateService.getAffiliatesMatching;
			
			$scope.selectComparisonAffiliate = function(affiliate) {
				$scope.showComparisonAffiliate = true;
				$scope.application = $scope.comparisonAffiliate.application;
				$log.log(affiliate, $scope.comparisonAffiliate);
			};
		} ]);
