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
		'AuditsService',
		function($scope, $log, $routeParams, $modal, $location, Session, UserRegistrationService, DomainBlacklistService,
				PackagesService, ApplicationUtilsService, AuditsService) {

			var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
			
			$scope.application = {};
			$scope.audits = [];
			
			$scope.commercialUsageInstitutionsByCountry = {};
			$scope.usageCountryCountslist = [];
			
			$scope.alerts = [];
			$scope.submitting = false;
			
			$scope.isNoteReadOnly = true;
			$scope.isActionDisabled = true;
			$scope.showNonMemberAlert = false;
			$scope.showNonPendingAlert = false;

			function loadApplicationAudits(applicationId) {
	          	AuditsService.findByApplicationId(applicationId)
            	.then(function(result) {
            		$scope.audits = result;
            	})
    			["catch"](function(message) {
    				$scope.alerts.push({type: 'danger', msg: 'Network request failure retrieving audit logs, please try again later.'});
    				$log.error('Failed to update audit list: '+message);
    			});
	        }

			function loadApplication() {
				//$log.log("load application...");
				var queryPromise = UserRegistrationService.getApplicationById(applicationId);

				queryPromise.success(function(application) {
						//$log.log("getapplicationbyid.... success");
						//$log.log('loadApplication', application);
						$scope.application = application;
						
						if (Session.isAdmin() || (Session.member.key === application.member.key)) {
							$scope.isNoteReadOnly = false;
							$scope.isActionDisabled = false;
						} else {
							$scope.showNonMemberAlert = true;
						}
						
						if (!ApplicationUtilsService.isApplicationPending(application)) {
							 $scope.showNonPendingAlert = true;
						}
						
						loadApplicationAudits(application.applicationId);

						if (application.affiliate) {
							$scope.standingState = application.affiliate.standingState;
						}
						
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
						//$log.log("getapplicatiobyid... success end");
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
