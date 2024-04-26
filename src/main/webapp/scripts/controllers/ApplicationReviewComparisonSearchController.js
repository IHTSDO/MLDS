'use strict';

mldsApp.controller('ApplicationReviewComparisonSearchController', [
		'$scope',
		'$log',
		'AffiliateService',
		function($scope, $log, AffiliateService) {
			$scope.showComparisonAffiliate = false; // start with search box showing

			/*$scope.getAffiliatesMatching = function getAffiliatesMatching(text) {
				var notSelfPredicate = function (affiliate) {
					return $scope.originalAffiliate === null
						|| affiliate.affiliateId !== $scope.originalAffiliate.affiliateId;
				};
				return AffiliateService.affiliatesResource.query({q:text}).$promise
					.then(function(foundAffiliates) {
					return _.first(_.filter(foundAffiliates, notSelfPredicate),10);
				});
			};*/
/*MLDS-996 - Front End Bug*/
			$scope.getAffiliatesMatching = function(text) {
			    var notSelfPredicate = function (affiliate) {
			        return $scope.originalAffiliate === null
			            || affiliate.affiliateId !== $scope.originalAffiliate.affiliateId; };
			    return AffiliateService.affiliatesResource.query({q: text}).$promise
			       .then(function(foundAffiliates) {
			        var foundResult = _.filter(foundAffiliates, notSelfPredicate).slice(0, 10);
			        return foundResult[0];
			});
			 };
/*MLDS-996 - Front End Bug*/
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
