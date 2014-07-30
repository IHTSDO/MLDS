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
				AffiliateService.affiliatesResource.query({q:''}).$promise
					.then(function(response) {
						$log.log('response', response, response.length);
						$scope.affiliates = response;
						_.sortBy($scope.affiliates, function(record) {
							return record.creator;
						});
					})
					["catch"](function(message) {
						$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					});
			}

			loadAffiliates();
			
			$scope.$watch('showAllAffiliates', toggleAffiliates);
			
			function toggleAffiliates() {
				if ($scope.affiliatesFilter) {
					$scope.affiliatesFilter = '';
				} else {
					var memberKey = Session.member && Session.member.key || 'NONE';
					$scope.affiliatesFilter = {'homeMember': memberKey};
				}
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
