'use strict';

angular.module('MLDS').controller('PendingApplicationsController', [
		'$scope',
		'$log',
		'$location',
		'$parse',
		'Session',
		'UserRegistrationService',
		'DomainBlacklistService',
		'PackagesService',
		'AffiliateService',
		function($scope, $log, $location, $parse, Session, UserRegistrationService, DomainBlacklistService,
				PackagesService, AffiliateService) {

			$scope.applications = [];
			$scope.showAllApplications = UserRegistrationService.pendingApplicationsFilter.showAllApplications ? UserRegistrationService.pendingApplicationsFilter.showAllApplications : 0;
			$scope.isAdmin = Session.isAdmin();
			$scope.homeMember = Session.member || {member: 'NONE'};

			$scope.downloadingApplications = false;
			$scope.page = 0;
			$scope.loadReset = false;
			$scope.noResults = false;

			$scope.orderByField = UserRegistrationService.pendingApplicationsFilter.orderByField ? UserRegistrationService.pendingApplicationsFilter.orderByField : 'submittedAt';
			$scope.reverseSort = UserRegistrationService.pendingApplicationsFilter.reverseSort ? UserRegistrationService.pendingApplicationsFilter.reverseSort : false;

			$scope.generatingCsv = false;

			function rememberVisualState() {
				var store = UserRegistrationService.pendingApplicationsFilter;
				
				store.showAllApplications = $scope.showAllApplications;
				store.orderByField = $scope.orderByField;
				store.reverseSort = $scope.reverseSort;
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
			
			$scope.generateCsv = function() {
				$scope.generatingCsv = true;
				return UserRegistrationService.filterPendingApplications($scope.query, 0, 999999999, $scope.showAllApplications==1?null:$scope.homeMember, $scope.orderByField, $scope.reverseSort)
					.then(function(response) {
						var expressions = [
						    $parse("application.applicationId"),
						    $parse("application.affiliateDetails.firstName + ' '+application.affiliateDetails.lastName"),
						    $parse("application.applicationType | enum:'application.applicationType.'"),
						    $parse("isPrimary ? ((application.affiliateDetails.agreementType | enum:'affiliate.agreementType.')||'') : ((application.affiliate.affiliateDetails.agreementType | enum:'affiliate.agreementType.')||'')"),
						    $parse("isPrimary ? (((application.type | enum:'affiliate.type.')||'') + ' - '+ ((application.subType | enum:'affiliate.subType.')||'')) : ((application.affiliate.type | enum:'affiliate.type.')||'')"),
						    $parse("application.submittedAt | date: 'yyyy-MM-dd'"),
						    $parse("application.approvalState | enum:'approval.state.'"),
						    $parse("(application.affiliateDetails.address.country.commonName)||''"),
						    $parse("(application.member.key | enum:'global.member.')||''"),
						    $parse("application.affiliateDetails.email")
						];
						var result = [];
						_.each(response.data, function(application) {
							var row = [];
							_.each(expressions, function(expression) {
								row.push(expression({
									'application':application,
									'isPrimary': application.applicationType === 'PRIMARY',
									'isExtension': application.applicationType === 'EXTENSION'
									}));
							});
							result.push(row);
						});
						$scope.generatingCsv = false;
						return result;
					})
					["catch"](function(message) {
						$scope.generatingCsv = false;
						$log.log("csv generation failure: "+message);
						$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					});
			};
			
		} ]);
