'use strict';

mldsApp.controller('AffiliatesController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'Session',
		'DomainBlacklistService',
		'PackagesService',
		'AffiliateService',
		function($scope, $log, $location, $modal, Session, DomainBlacklistService,
				PackagesService, AffiliateService) {

			$scope.affiliates = [];
			$scope.showAllAffiliates = 0;
			$scope.affiliatesFilter = '';

			$scope.alerts = [];

			function loadAffiliates() {
				$scope.affiliates = AffiliateService.affiliatesResource.query({q:''});
				$scope.affiliates.$promise
					.then(function(response) {
						toggleAffiliates();
					})
					["catch"](function(message) {
						$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					});
			}

			loadAffiliates();
			
			$scope.$watch('showAllAffiliates', toggleAffiliates);
			
			function toggleAffiliates() {
				$log.log('$scope.showAllAffiliates', $scope.showAllAffiliates);
				if ($scope.showAllAffiliates == 1) {
					$scope.affiliatesFilter = '';
				} else {
					var memberKey = Session.member && Session.member.key || 'NONE';
					$scope.affiliatesFilter = {'homeMember': memberKey};
				}
			};
			
			$scope.affiliateActiveDetails = function(affiliate) {
				return affiliate.affiliateDetails || (affiliate.application && affiliate.application.affiliateDetails) || {};
			};
			
			$scope.viewApplication = function(application) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/admin/applicationSummaryModal.html',
        			controller: 'ApplicationSummaryModalController',
        			size:'lg',
        			backdrop: 'static',
        			resolve: {
        				application: function() {
        					return application;
        				}
        			}
        		});

			};
		} ]);
