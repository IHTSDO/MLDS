'use strict';

angular.module('MLDS').controller('PendingApplicationsController', [
		'$scope',
		'$log',
		'$location',
		'Session',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'AffiliateService',
		function($scope, $log, $location, Session, UserRegistrationService, DomainBlacklistService,
				PackagesService, AffiliateService) {

			$scope.applications = [];
			$scope.showAllApplications = 0;
			$scope.isAdmin = Session.isAdmin();
			$scope.homeMember = Session.member || {member: 'NONE'};

			$scope.downloadingApplications = false;
			$scope.page = 0;
			$scope.loadReset = false;
			$scope.noResults = false;

			$scope.orderByField = 'submittedAt';
			$scope.reverseSort = false;

			function rememberVisualState() {
				//TODO implement
			}
			
			function loadMoreApplications() {
				rememberVisualState();
				
				if ($scope.downloadingApplications) {
					// If a loadAffiliates (loadReset === true) had been called then need to redownload once the current download is complete
					return;
				}
				$scope.loadReset = false;
				$scope.downloadingApplications = true;
				$scope.alerts = [];
				UserRegistrationService.filterPendingApplications($scope.query, $scope.page, 50, $scope.showAllApplications==1?null:$scope.homeMember, $scope.orderByField, $scope.reverseSort)
					.then(function(response) {
						//$log.log("...appending "+response.data.length+" to existing "+$scope.applications.length+" page="+$scope.page);
						_.each(response.data, function(a) {
							$scope.applications.push(a);
						});
						if (_.size($scope.applications) > 0) {
							$scope.noResults = false;
						}
						$scope.page = $scope.page + 1;
						$scope.downloadingApplications = false;
						if ($scope.loadReset) {
							loadApplications();
						}
					})
					["catch"](function(message) {
						$scope.downloadingApplications = false;
						$log.log("affiliates download failure: "+message);
						$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
						if ($scope.loadReset) {
							loadApplications();
						}
					});
			}
			
			function loadApplications() {
				//Force clear - note loadMoreApplications works on alias
				$scope.applications = [];
				$scope.page = 0;
				$scope.noResults = true;
				$scope.canSort = !$scope.query;
				
				$scope.loadReset = true;
				
				loadMoreApplications();
			}

			loadApplications();
			
			$scope.$watch('showAllApplications', loadApplications);
			
			$scope.nextPage = function() {
				return loadMoreApplications();
			};

			$scope.toggleField = function(fieldName) {
				if ($scope.orderByField !== fieldName) {
					$scope.reverseSort = false;
				} else {
					$scope.reverseSort = !$scope.reverseSort;
				}
				$scope.orderByField = fieldName; 
				loadApplications();
			};

			
			$scope.goToApplication = function(application) {
				$log.log('application', application);
				$location.path('/applicationReview/'+encodeURIComponent(application.applicationId));
			};
			
			$scope.totalSublicenses = function(usage) {
					var count = 0;
					if (usage.entries) {
						count += usage.entries.length;
					}
					if (usage.countries) {
						count += usage.countries.reduce(function(total, c) {
		        			return total + (c.snomedPractices || 0);
		        		}, 0);
					}
					return count;
			};
		} ]);
