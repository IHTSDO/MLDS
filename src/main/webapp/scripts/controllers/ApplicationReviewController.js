'use strict';

mldsApp.controller('ApplicationReviewController', [
		'$scope',
		'$log',
		'$routeParams',
		'$modal',
		'$location',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'LicenseeService',
		function($scope, $log, $routeParams, $modal, $location, UserRegistrationService, DomainBlacklistService,
				PackagesService, LicenseeService) {

			var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
			
			$scope.pending = {
					application : {},
					licensee : {},
					usage : {}
			};
			
			$scope.commercialUsageInstitutionsByCountry = {};
			$scope.usageCountryCountslist = [];
			
			$scope.alerts = [];
			$scope.submitting = false;

			function loadApplication() {
				var queryPromise = UserRegistrationService.getApplicationById(applicationId);

				queryPromise.success(function(application) {
						if (!UserRegistrationService.isApplicationPending(application)) {
							$log.log('Application not in pending state');
							goToPendingApplications();
							return;
						}
						
						$scope.pending.application = application;
						$scope.pending.usage = application.commercialUsage;
						
						if (application.commercialUsage) {
							$scope.commercialUsageInstitutionsByCountry = _.groupBy(application.commercialUsage.entries, 
			        				function(entry){ return entry.country.isoCode2;});
							_.each($scope.commercialUsageInstitutionsByCountry, function(list, key) {
								$scope.commercialUsageInstitutionsByCountry[key] = _.sortBy(list, function(entry) {
									return entry.name.toLowerCase();
									});
							});
							$scope.usageCountryCountslist = _.sortBy(application.commercialUsage.countries, function(count) {
								return count.country.commonName.toLowerCase();
							});
						}
				});
			}

			loadApplication();
        	
			$scope.submit = function() {
				$scope.alerts.splice(0, $scope.alerts.length);
				$scope.submitting = true;
				
				UserRegistrationService.updateApplicationNoteInternal($scope.pending.application)
					.then(function(result) {
						$scope.submitting = false;
					})
					["catch"](function(message) {
						$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
						$scope.submitting = false;
					});
			};

			$scope.approveApplication = function() {
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/approveApplicationModal.html',
					controller: 'ApproveApplicationModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.pending.application;
						}
					}
				});
				modalInstance.result
				.then(function(result) {
					$modal.open({
						templateUrl: 'views/admin/approveApplicationConfirmationModal.html',
						controller: 'ApproveApplicationConfirmationModalController',
						backdrop: 'static',
						resolve: {
							application: function() {
								return $scope.pending.application;
							}
						}
					}).result.then(function(result) {
						goToPendingApplications();						
					})
					["catch"](function(message) {
						goToPendingApplications();	
					});
				});
			};

			$scope.rejectApplication = function() {
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/rejectApplicationModal.html',
					controller: 'RejectApplicationModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.pending.application;
						}
					}
				});
				modalInstance.result
				.then(function(result) {
					goToPendingApplications();
				});
			};

			$scope.reviewRequested = function() {
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/reviewRequestedModal.html',
					controller: 'ReviewRequestedModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.pending.application;
						}
					}
				});
				modalInstance.result
				.then(function(result) {
					goToPendingApplications();
				});
			};

			$scope.changeRequested = function() {
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/changeRequestedModal.html',
					controller: 'ChangeRequestedModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.pending.application;
						}
					}
				});
				modalInstance.result
				.then(function(result) {
					goToPendingApplications();
				});
			};

			function goToPendingApplications() {
				$location.path('/pendingApplications');
			}
		} ]);
