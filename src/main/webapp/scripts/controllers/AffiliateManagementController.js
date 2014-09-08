'use strict';

mldsApp.controller('AffiliateManagementController', [
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
			$scope.homeMember = Session.member || {member: 'NONE'};
			$scope.isAdmin = Session.isAdmin();
			$scope.downloadingAffiliates = false;
			$scope.query = '';
			$scope.page = 0;

			$scope.alerts = [];

			function loadMoreAffiliates() {
				if ($scope.downloadingAffiliates) {
					return;
				}
				$scope.downloadingAffiliates = true;
				$scope.alerts = [];
				//$log.log("wanting existing "+$scope.affiliates.length+" page="+$scope.page);
				AffiliateService.filterAffiliates($scope.query, $scope.page, 50, $scope.showAllAffiliates==1?null:$scope.homeMember)
					.then(function(response) {
						//$log.log("...appending "+response.data.length+" to existing "+$scope.affiliates.length+" page="+$scope.page);
						_.each(response.data, function(a) {
							$scope.affiliates.push(a);
						});
						$scope.page = $scope.page + 1;
						$scope.downloadingAffiliates = false;
					})
					["catch"](function(message) {
						$scope.downloadingAffiliates = false;
						$log.log("affiliates download failure: "+message);
						$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					});
			}
			
			function loadAffiliates() {
				//Force clear - note loadMoreAffiliates works on alias
				$scope.affiliates = [];
				$scope.page = 0;
				
				loadMoreAffiliates();
			}

			$scope.$watch('showAllAffiliates', loadAffiliates);
			$scope.$watch('query', loadAffiliates);
						
			$scope.nextPage = function() {
				return loadMoreAffiliates();
			};
			
			$scope.affiliateActiveDetails = function(affiliate) {
				return affiliate.affiliateDetails 
					|| (affiliate.application && affiliate.application.affiliateDetails) 
					|| {};
			};
			
			$scope.viewAffiliate = function viewAffiliate(affiliateId) {
				$location.path('/affiliateManagement/' + affiliateId);
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
        				},
        				audits: function() {
        					return [];
        				}
        			}
        		});
			};
		} ]);
