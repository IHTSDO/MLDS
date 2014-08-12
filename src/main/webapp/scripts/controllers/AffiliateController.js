'use strict';

mldsApp.controller('AffiliateController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'$routeParams',
		'Session',
		'AffiliateService',
		'ApplicationUtilsService',
		function($scope, $log, $location, $modal, $routeParams, Session, AffiliateService, ApplicationUtilsService) {
			
			var affiliateId = $routeParams.affiliateId && parseInt($routeParams.affiliateId, 10);
			
			$scope.alerts = [];
			$scope.affiliate = {};
			$scope.affiliateDetails = {};
			$scope.approved = false;
			$scope.readOnly = false;

			function loadAffiliate() {
				var queryPromise = AffiliateService.affiliate(affiliateId);
				
				queryPromise.success(function(affiliate) {
					$scope.affiliate = affiliate;
					$scope.approved = ApplicationUtilsService.isApplicationApproved(affiliate.application);
					$scope.isEditable = Session.isAdmin() || (Session.member.key == affiliate.application.member.key);
					$scope.readOnly = !ApplicationUtilsService.isApplicationApproved(affiliate.application) || !$scope.isEditable;
					
					if (affiliate.affiliateDetails) {
						$scope.affiliateDetails = affiliate.affiliateDetails;
					}
				});
					
			}

			loadAffiliate(); 
			
			$scope.viewApplication = function(application) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/admin/applicationSummaryModal.html',
        			controller: 'ApplicationSummaryModalController',
        			size:'lg',
        			resolve: {
        				application: function() {
        					return application;
        				}
        			}
        		});
			};
		} ]);
