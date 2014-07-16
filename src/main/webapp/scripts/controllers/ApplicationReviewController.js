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
				// FIXME should be replaced by API call
				var queryPromise = UserRegistrationService.getApplications();

				queryPromise.success(function(data) {
					//FIXME temporary loop
					data.forEach(function(application, index) {
						if (application.applicationId !== applicationId) {
							return;
						} 
						$scope.pending.application = application;
						LicenseeService.licensees(application.username)
							.then(function(result) {
								if (result.data.length > 0) {
									var licensee = result.data[0];
									$scope.pending.licensee = licensee;
									//FIXME choose most recent
									if (licensee.commercialUsages.length > 0) {
										var usageReport = null;
										licensee.commercialUsages.forEach(function(current) {
											if (!usageReport || (new Date(current.startDate).getTime() > new Date(usageReport.startDate).getTime())) {
												usageReport = current;
											}
										});
										$scope.pending.usage = usageReport;
										if (usageReport) {
											$scope.commercialUsageInstitutionsByCountry = _.groupBy(usageReport.entries, 
							        				function(entry){ return entry.country.isoCode2;});
											_.each($scope.commercialUsageInstitutionsByCountry, function(list, key) {
												$scope.commercialUsageInstitutionsByCountry[key] = _.sortBy(list, function(entry) {
													return entry.name.toLowerCase();
													});
											});
											$scope.usageCountryCountslist = _.sortBy(usageReport.countries, function(count) {
												return count.country.commonName.toLowerCase();
											});
										}
									}
								}
								$log.log($scope.pending);
							})
							["catch"](function(message) {
								$log.log('failed to retrieve licensee', message);
							});
					});
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
					$location.path('/pendingApplications');
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
					$location.path('/pendingApplications');
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
					$location.path('/pendingApplications');
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
					$location.path('/pendingApplications');
				});
			};

		} ]);
