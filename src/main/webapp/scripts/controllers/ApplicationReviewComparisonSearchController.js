'use strict';

mldsApp.controller('ApplicationReviewComparisonSearchController', [
		'$scope',
		'$log',
		'AffiliateService',
		function($scope, $log, AffiliateService) {
			$scope.showComparisonAffiliate = false; // start with search box showing
			
			$scope.getAffiliatesMatching = function getAffiliatesMatching(text) {
				var notSelfPredicate = function (affiliate) {
					return $scope.originalAffiliate === null 
						|| affiliate.affiliateId !== $scope.originalAffiliate.affiliateId;
				};
				return AffiliateService.affiliatesResource.query({q:text}).$promise
					.then(function(foundAffiliates) {
					return _.first(_.filter(foundAffiliates, notSelfPredicate),10);
				});
			};
			
			$scope.selectComparisonAffiliate = function selectComparisonAffiliate(affiliate) {
				$scope.showComparisonAffiliate = true;
				$scope.application = $scope.comparisonAffiliate.application;
				$scope.standingState = $scope.comparisonAffiliate.standingState;
				$scope.audits = [];
			};
			
			$scope.setOriginalAffiliate = function setOriginalAffiliate(affiliate) {
				$scope.originalAffiliate = affiliate;
			};
		} ]);
