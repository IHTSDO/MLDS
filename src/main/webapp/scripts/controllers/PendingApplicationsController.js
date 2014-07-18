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

			$scope.applications = [];

			function loadApplications() {
				// FIXME should be replaced by API call
				var queryPromise = UserRegistrationService.getApplicationsPending();

				queryPromise.success(function(data) {
					$scope.applications = data;
					_.sortBy($scope.pplications, function(record) {
						return record.submittedAt;
					});
				});
			}

			loadApplications();

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
