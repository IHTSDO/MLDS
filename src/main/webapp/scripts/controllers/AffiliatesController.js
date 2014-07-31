'use strict';

mldsApp.controller('AffiliatesController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'Session',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'AffiliateService',
		function($scope, $log, $location, $modal, Session, UserRegistrationService, DomainBlacklistService,
				PackagesService, AffiliateService) {

			$scope.affiliates = [];
			$scope.showAllAffiliates = false;
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
			
			$scope.$watch('showAllAffiliates', function() {
				if ($scope.affiliates.$resolved) {
					toggleAffiliates();
				}
			});
			
			function toggleAffiliates() {
				if ($scope.affiliatesFilter) {
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
