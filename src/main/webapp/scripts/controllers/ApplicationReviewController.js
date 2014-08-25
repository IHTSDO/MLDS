'use strict';

mldsApp.controller('ApplicationReviewController', [
		'$scope',
		'$log',
		'$routeParams',
		'$modal',
		'$location',
		'Session',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'ApplicationUtilsService',
		function($scope, $log, $routeParams, $modal, $location, Session, UserRegistrationService, DomainBlacklistService,
				PackagesService, ApplicationUtilsService) {

			var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
			
			$scope.application = {};
			
			$scope.commercialUsageInstitutionsByCountry = {};
			$scope.usageCountryCountslist = [];
			
			$scope.alerts = [];
			$scope.submitting = false;
			$scope.isReadOnly = true;

			function loadApplication() {
				$log.log("load application...");
				var queryPromise = UserRegistrationService.getApplicationById(applicationId);

				queryPromise.success(function(application) {
						$log.log("getapplicationbyid.... success");
						if (!ApplicationUtilsService.isApplicationPending(application)) {
							$log.log('Application not in pending state');
							goToPendingApplications();
							return;
						}
						//$log.log('loadApplication', application);
						$scope.application = application;
						
						$scope.isReadOnly = !Session.isAdmin() && (Session.member.key !== application.member.key);
						
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
						$log.log("getapplicatiobyid... success end");
				});
			}

			loadApplication();
        	
			$scope.submit = function() {
				$scope.alerts.splice(0, $scope.alerts.length);
				$scope.submitting = true;
				
				UserRegistrationService.updateApplicationNoteInternal($scope.application)
					.then(function(result) {
						$scope.submitting = false;
					})
					["catch"](function(message) {
						$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
						$scope.submitting = false;
					});
			};

			$scope.approveApplication = function() {
				$scope.submit();
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/approveApplicationModal.html',
					controller: 'ApproveApplicationModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.application;
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
								return $scope.application;
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
				$scope.submit();
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/rejectApplicationModal.html',
					controller: 'RejectApplicationModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.application;
						}
					}
				});
				modalInstance.result
				.then(function(result) {
					goToPendingApplications();
				});
			};

			$scope.reviewRequested = function() {
				$scope.submit();
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/reviewRequestedModal.html',
					controller: 'ReviewRequestedModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.application;
						}
					}
				});
				modalInstance.result
				.then(function(result) {
					goToPendingApplications();
				});
			};

			$scope.changeRequested = function() {
				$scope.submit();
				var modalInstance = $modal.open({
					templateUrl: 'views/admin/changeRequestedModal.html',
					controller: 'ChangeRequestedModalController',
					backdrop: 'static',
					resolve: {
						application: function() {
							return $scope.application;
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
