'use strict';

mldsApp.controller('PendingApplicationsController', [
		'$scope',
		'$log',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'LicenseeService',
		function($scope, $log, UserRegistrationService, DomainBlacklistService,
				PackagesService, LicenseeService) {

			$scope.pendingApplications = [];

			function getApplications() {
				// FIXME should be replaced by API call
				var queryPromise = UserRegistrationService.getApplications();

				queryPromise.success(function(data) {
					data.forEach(function(application, index) {
						if (!UserRegistrationService.isApplicationReadyToProcess(application)) {
							return
						}
						var record = {
							application : application,
							licensee : {},
							usage : {}
						};
						$scope.pendingApplications.push(record);
						LicenseeService.licensees(application.username)
							.then(function(result) {
								if (result.data.length > 0) {
									var licensee = result.data[0];
									record.licensee = licensee;
									//FIXME choose most recent
									if (licensee.commercialUsages.length > 0) {
										var usageReport = null;
										licensee.commercialUsages.forEach(function(current) {
											if (!usageReport || (new Date(current.startDate).getTime() > new Date(usageReport.startDate).getTime())) {
												usageReport = current;
											}
										});
										record.usage = usageReport;
									}
								}
							})
							["catch"](function(message) {
								$log.log('failed to retrieve licensee', message);
							});
					});
					_.sortBy($scope.pendingApplications, function(record) {
						return record.application.submittedAt;
					});
				});
			}

			getApplications();

			$scope.goToApplication = function(application) {
				// FIXME set location
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
