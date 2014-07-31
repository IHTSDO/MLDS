'use strict';

angular.module('MLDS').controller('UsageReportsController',
		['$scope', '$location', '$log', 'AffiliateService', 'UserRegistrationService',
    function ($scope, $location, $log, AffiliateService, UserRegistrationService) {
			$scope.affiliates = [];

        	function loadAffiliates() {
        		AffiliateService.myAffiliates()
	        		.then(function(affiliatesResult) {
	        			var someApplicationsWaitingForApplicant = _.some(affiliatesResult.data, function(affiliate) {
	        				return UserRegistrationService.isApplicationWaitingForApplicant(affiliate.application);
	        			});
	        			if (someApplicationsWaitingForApplicant) {
	        				$location.path('/affiliateRegistration');
	        				return;
	        			}
	        			$scope.affiliates = affiliatesResult.data;
	        			
	        			affiliatesResult.data.forEach(function(affiliate) {
	        				affiliate.commercialUsages.sort(function(a, b) {
	        					if (a.startDate && b.startDate) {
	        						return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
	        					} else if (a.startDate) {
	        						return 1;
	        					} else {
	        						return -1;
	        					}
	        				});
	        			});
	
	        		});
        	}

        	loadAffiliates();
        	
        	$scope.usageReportCountries = function(usageReport) {
        		return usageReport.countries.length;
        	};

        	$scope.usageReportHospitals = function(usageReport) {
        		return usageReport.entries.length;
        	};

        	$scope.usageReportPractices = function(usageReport) {
        		return usageReport.countries.reduce(function(total, count) {
        			return total + (count.practices || 0);
        		}, 0);
        	};

        	$scope.openAddUsageReportModal = function(affiliate) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/user/addUsageReportModal.html',
        			controller: 'AddUsageReportController',
        			size:'lg',
        			backdrop: 'static',
        			resolve: {
        				affiliateId: function() {
        					return affiliate.affiliateId;
        				}
        			}
        		});
        	};

        	$scope.goToUsageReport = function(usageReport) {
        		$location.path('/usage-log/'+encodeURIComponent(usageReport.commercialUsageId));
        	};
        	
        	$scope.affiliateIsCommercial = function(affiliate) {
        		return AffiliateService.affiliateIsCommercial(affiliate);
        	};
        	
        	$scope.anySubmittedUsageReports = function(affiliate) {
        		return _.some(affiliate.commercialUsages, function(usageReport) {
        			return usageReport.approvalState !== 'NOT_SUBMITTED';
        		});
        	};
        	
        	$scope.isApplicationPending = function(application) {
        		return UserRegistrationService.isApplicationPending(application);
        	};
        	
        	$scope.isApplicationWaitingForApplicant = function(application) {
        		return UserRegistrationService.isApplicationWaitingForApplicant(application);
        	};
    }]);

