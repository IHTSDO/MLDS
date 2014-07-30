'use strict';

mldsApp.controller('PendingApplicationsController', [
        '$rootScope',
		'$scope',
		'$log',
		'$location',
		'Session',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'AffiliateService',
		function($rootScope, $scope, $log, $location, Session, UserRegistrationService, DomainBlacklistService,
				PackagesService, AffiliateService) {

			$scope.applications = [];
			$scope.showAllApplications = false;
			$scope.applicationSearch = '';


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
			
			$scope.$watch('showAllApplications', toggleApplications);
			
			function toggleApplications() {
				if ($scope.showAllApplications) {
					$scope.applicationSearch = '';
				} else {
					var memberKey = Session.member && Session.member.key || 'NONE';
					$scope.applicationSearch = {'member': memberKey};
				}
			};


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
