'use strict';

mldsApp.controller('AffiliateSummaryController', [
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
			$scope.approved = false;
			$scope.isApplicationApproved = ApplicationUtilsService.isApplicationApproved;

			$scope.alerts = [];
			$scope.submitting = false;

			function loadAffiliate() {
				var queryPromise = AffiliateService.affiliate(affiliateId);
				
				queryPromise.success(function(affiliate) {
					$scope.affiliate = affiliate;
					$scope.approved = $scope.isApplicationApproved(affiliate.application);
					$scope.isEditable = Session.isAdmin() || (Session.member.key == affiliate.application.member.key);
				});
					
			}

			loadAffiliate(); 
			
			$scope.editAffiliate = function editAffiliate() {
				$location.path('/affiliates/' + affiliateId + '/edit')
			};
			
			$scope.viewApplication = function viewApplication(application) {
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
			
			$scope.approveApplication = function approveApplication(application) {
				$location.path('/applicationReview/' + application.applicationId);
			};
			
			$scope.submit = function submit() {
				$scope.alerts.splice(0, $scope.alerts.length);
				$scope.submitting = true;
				
				AffiliateService.updateAffiliate($scope.affiliate)
				.then(function(result) {
					$scope.submitting = false;
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
			};
			
			$scope.createLogin = function createLogin(){
				if (!$scope.affiliate.affiliateDetails.email) {
					$log.log('no email!');
					createLoginModal({noEmail: 'true'});
					return;
				}
				
				requestLoginCreation();
			};
			
			function requestLoginCreation() {
				AffiliateService.createLogin($scope.affiliate)
				.success(function(result) {
					$log.log('login created');
					$scope.alerts.push({type: 'success', msg: 'Login has been successfully created for user.'});
					loadAffiliate();
				})
				.error(function(result) {
					$log.log('error open modal');
					createLoginModal({duplicateEmail: 'true'});
				});
			};
			
			
			function createLoginModal(reason) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/admin/createLoginModal.html',
        			controller: 'CreateLoginModalController',
        			size:'sm',
        			resolve: {
        				affiliate: function() {
        					return $scope.affiliate;
        				},
        				reason: function() {
        					return reason;
        				}
        			}
        		});
        		
        		modalInstance.result.then(function () {
        			requestLoginCreation();
    		    });
			};
			
		} ]);
