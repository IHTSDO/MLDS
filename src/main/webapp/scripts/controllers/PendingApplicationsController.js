'use strict';

mldsApp.controller('PendingApplicationsController', [
		'$scope',
		'$log',
		'$location',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'LicenseeService',
		function($scope, $log, $location, UserRegistrationService, DomainBlacklistService,
				PackagesService, LicenseeService) {

			$scope.pendingApplications = [];

			function getApplications() {
				// FIXME should be replaced by API call
				var queryPromise = UserRegistrationService.getApplicationsPending();

				queryPromise.success(function(data) {
					data.forEach(function(application, index) {
						if (!UserRegistrationService.isApplicationPending(application)) {
							$log.log('failed pending...', application);
							return
						}
						var record = {
							application : application,
							licensee : {},
							usage : application.commercialUsage
						};
						$scope.pendingApplications.push(record);
					});
					_.sortBy($scope.pendingApplications, function(record) {
						return record.application.submittedAt;
					});
				});
			}

			getApplications();

			$scope.goToApplication = function(application) {
				$log.log('application', application);
				$location.path('/applicationReview/'+encodeURIComponent(application.applicationId));
			};
			
			$scope.totalSublicences = function(usage) {
					var count = 0;
					if (usage.entries) {
						count += usage.entries.length;
					}
					if (usage.countries) {
						count += usage.countries.reduce(function(total, c) {
		        			return total + (c.practices || 0);
		        		}, 0);
					}
					return count;
			};
		} ]);
