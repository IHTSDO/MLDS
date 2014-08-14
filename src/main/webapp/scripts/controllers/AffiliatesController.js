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
						$log.log('affiliates complete', response);
						toggleAffiliates();
					})
					["catch"](function(message) {
						$log.log("affitilates failure: "+message);
						$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					});
			}

			loadAffiliates();
			
			$scope.search = function (affiliate) {
				var affiliateDetails = $scope.affiliateActiveDetails(affiliate);
				if (!affiliateDetails || !affiliateDetails.affiliateDetailsId) {
					return false;
				}
		        return !!((
		        		(affiliateDetails.organizationName && affiliateDetails.organizationName.toLowerCase().indexOf($scope.query || '') !== -1)
		        		|| affiliateDetails.firstName.toLowerCase().indexOf($scope.query || '') !== -1
		        		|| affiliateDetails.lastName.toLowerCase().indexOf($scope.query || '') !== -1
		        		|| affiliateDetails.email.toLowerCase().indexOf($scope.query || '') !== -1
		        		|| affiliateDetails.address.country.commonName.toLowerCase().indexOf($scope.query || '') !== -1
		        		|| affiliate.homeMember.key.toLowerCase().indexOf($scope.query || '') !== -1
		        		));
		    };
			
			$scope.$watch('showAllAffiliates', toggleAffiliates);
			
			function toggleAffiliates() {
				if ($scope.showAllAffiliates == 1) {
					$scope.affiliatesFilter = '';
				} else {
					var memberKey = Session.member && Session.member.key || 'NONE';
					$scope.affiliatesFilter = {'homeMember': memberKey};
				}
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
