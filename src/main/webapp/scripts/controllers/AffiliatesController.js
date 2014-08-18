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
			$scope.homeMember = Session.member || {member: 'NONE'};
			$scope.downloadingAffiliates = false;
			$scope.query = '';

			$scope.alerts = [];

			function loadMoreAffiliates() {
				if ($scope.downloadingAffiliates) {
					return;
				}
				$scope.downloadingAffiliates = true;
				$scope.alerts = [];
				var capturedAffiliates = $scope.affiliates;
				AffiliateService.filterAffiliates($scope.query, 50, capturedAffiliates.length||0, $scope.showAllAffiliates==1?null:$scope.homeMember)
					.then(function(response) {
						_.each(response.data, function(a) {
							capturedAffiliates.push(a);
						});
						$scope.downloadingAffiliates = false;
					})
					["catch"](function(message) {
						$scope.downloadingAffiliates = false;
						$log.log("affiliates download failure: "+message);
						$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					});
			}
			
			function loadAffiliates() {
				//Force clear - note loadMoreAffiliates works on alias
				$scope.affiliates = [];
				$scope.downloadingAffiliates = false;
				
				loadMoreAffiliates();
			}

			loadAffiliates();
			
			$scope.$watch('showAllAffiliates', loadAffiliates);
			$scope.$watch('query', loadAffiliates);
						
			$scope.nextPage = function() {
				loadMoreAffiliates();
			};
			
			$scope.affiliateActiveDetails = function(affiliate) {
				return affiliate.affiliateDetails 
					|| (affiliate.application && affiliate.application.affiliateDetails) 
					|| {};
			};
			
			$scope.viewAffiliate = function viewAffiliate(affiliateId) {
				$location.path('/affiliates/' + affiliateId);
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
