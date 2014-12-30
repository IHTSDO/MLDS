'use strict';

mldsApp.controller('AffiliateManagementController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'$parse',
		'Session',
		'DomainBlacklistService',
		'PackagesService',
		'AffiliateService',
		'StandingStateUtils',
		function($scope, $log, $location, $modal, $parse, Session, DomainBlacklistService,
				PackagesService, AffiliateService, StandingStateUtils) {

			$scope.affiliates = [];
			$scope.showAllAffiliates = AffiliateService.affiliatesFilter.showAllAffiliates ? AffiliateService.affiliatesFilter.showAllAffiliates : 0;
			$scope.homeMember = Session.member || {member: 'NONE'};
			$scope.isAdmin = Session.isAdmin();
			$scope.downloadingAffiliates = false;
			$scope.query = AffiliateService.affiliatesFilter.affiliateQuery ? AffiliateService.affiliatesFilter.affiliateQuery : '';
			$scope.page = 0;
			
			$scope.orderByField = AffiliateService.affiliatesFilter.orderByField ? AffiliateService.affiliatesFilter.orderByField : 'standingState';
			$scope.reverseSort = AffiliateService.affiliatesFilter.reverseSort ? AffiliateService.affiliatesFilter.reverseSort : false;

			$scope.standingStateFilter = AffiliateService.affiliatesFilter.standingStateFilter ? AffiliateService.affiliatesFilter.standingStateFilter : null;
			
			$scope.canSort = true;
			$scope.standingStateOptions = StandingStateUtils.options();

			$scope.alerts = [];
			
			$scope.generatingCsv = false;

			function rememberVisualState() {
				AffiliateService.affiliatesFilter.affiliateQuery = $scope.query;
				AffiliateService.affiliatesFilter.showAllAffiliates = $scope.showAllAffiliates;
				AffiliateService.affiliatesFilter.orderByField = $scope.orderByField;
				AffiliateService.affiliatesFilter.reverseSort = $scope.reverseSort;
				AffiliateService.affiliatesFilter.standingStateFilter = $scope.standingStateFilter;
			}
			
			function loadMoreAffiliates() {
				rememberVisualState();
				
				if ($scope.downloadingAffiliates) {
					// If a loadAffiliates (loadReset === true) had been called then need to redownload once the current download is complete
					return;
				}
				$scope.loadReset = false;
				$scope.downloadingAffiliates = true;
				$scope.alerts = [];
				AffiliateService.filterAffiliates($scope.query, $scope.page, 50, $scope.showAllAffiliates==1?null:$scope.homeMember, $scope.standingStateFilter,$scope.orderByField, $scope.reverseSort)
					.then(function(response) {
						//$log.log("...appending "+response.data.length+" to existing "+$scope.affiliates.length+" page="+$scope.page);
						_.each(response.data, function(a) {
							$scope.affiliates.push(a);
						});
						if (_.size($scope.affiliates) > 0) {
							$scope.noResults = false;
						}
						$scope.page = $scope.page + 1;
						$scope.downloadingAffiliates = false;
						if ($scope.loadReset) {
							loadAffiliates();
						}
					})
					["catch"](function(message) {
						$scope.downloadingAffiliates = false;
						$log.log("affiliates download failure: "+message);
						$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
						if ($scope.loadReset) {
							loadAffiliates();
						}
					});
			}
			
			function loadAffiliates() {
				//Force clear - note loadMoreAffiliates works on alias
				$scope.affiliates = [];
				$scope.page = 0;
				$scope.noResults = true;
				$scope.canSort = !$scope.query;
				
				$scope.loadReset = true;
				
				loadMoreAffiliates();
			}
			
			$scope.clearSearch = function() {
				$scope.standingStateFilter = null;
				$scope.query = '';
			};

			$scope.toggleField = function(fieldName, isDescendingOrder) {
				if ($scope.orderByField !== fieldName) {
					$scope.reverseSort = false;
				} else {
					$scope.reverseSort = !$scope.reverseSort;
				}
				
				if (typeof(isDescendingOrder) !== 'undefined') {
					$scope.reverseSort = isDescendingOrder;
				}
				
				$scope.orderByField = fieldName; 
				loadAffiliates();
			};
			
			$scope.filterStandingState = function(state) {
				$scope.standingStateFilter = state;
				loadAffiliates();
			};
			
			$scope.$watch('showAllAffiliates', loadAffiliates);
			$scope.$watch('query', loadAffiliates);
						
			$scope.nextPage = function() {
				return loadMoreAffiliates();
			};
			
			$scope.affiliateActiveDetails = function(affiliate) {
				return affiliate.affiliateDetails 
					|| (affiliate.application && affiliate.application.affiliateDetails) 
					|| {};
			};
			
			$scope.viewAffiliate = function viewAffiliate(affiliateId) {
				$location.path('/affiliateManagement/' + affiliateId);
			};
			
			
			$scope.viewApplication = function(application) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/admin/applicationSummaryModal.html',
        			controller: 'ApplicationSummaryModalController',
        			size:'lg',
        			backdrop: 'static',
        			resolve: {
        				application: function() {
        					return application;
        				},
        				audits: function() {
        					return [];
        				}
        			}
        		});
			};

			$scope.generateCsv = function() {
				$scope.generatingCsv = true;
				return AffiliateService.filterAffiliates($scope.query, 0, 999999999, $scope.showAllAffiliates==1?null:$scope.homeMember, $scope.standingStateFilter,$scope.orderByField, $scope.reverseSort)
					.then(function(response) {
						var expressions = [
						    $parse("affiliate.affiliateId"),
						    $parse("affiliateActiveDetails.firstName + ' '+affiliateActiveDetails.lastName"),
						    $parse("((affiliateActiveDetails.type | enum:'affiliate.type.')||'') + ' - '+((affiliateActiveDetails.subType | enum:'affiliate.subType.')||'') + ' '+ (affiliateActiveDetails.otherText||'')"),
						    $parse("affiliate.standingState | enum:'affiliate.standingState.'"),
						    $parse("affiliateActiveDetails.address.country.commonName||''"),
						    $parse("affiliate.homeMember.key"),
						    $parse("affiliateActiveDetails.email")
						];
						var result = [];
						_.each(response.data, function(affiliate) {
							var row = [];
							_.each(expressions, function(expression) {
								row.push(expression({
									'affiliate': affiliate,
									'affiliateActiveDetails': $scope.affiliateActiveDetails(affiliate)
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
