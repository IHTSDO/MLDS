 'use strict';

angular.module('MLDS')
.factory('UsageReportsService', [ '$location', '$log', '$modal', 'AffiliateService', 'ApprovalStateUtils',
                                    function($location, $log, $modal, AffiliateService, ApprovalStateUtils){

	var service = {};
	
	service.usageReportCountries = function(usageReport) {
		return usageReport.countries.length;
	};

	service.usageReportHospitals = function(usageReport) {
		return usageReport.entries.length;
	};

	service.usageReportPractices = function(usageReport) {
		return usageReport.countries.reduce(function(total, count) {
			return total + (count.practices || 0);
		}, 0);
	};
	
	service.isUsageReportWaitingForApplicant = function(usageReport) {
		return ApprovalStateUtils.isWaitingForApplicant(usageReport.approvalState);
	};

	service.openAddUsageReportModal = function(affiliate) {
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

	service.goToUsageReport = function(usageReport) {
		$location.path('/usageReports/usageLog/'+encodeURIComponent(usageReport.commercialUsageId));
	};
	
	service.goToReviewUsageReport = function(usageReport) {
		$location.path('/usageReportsReview/'+encodeURIComponent(usageReport.commercialUsageId));
	};
	
	service.affiliateIsCommercial = function(affiliate) {
		return AffiliateService.affiliateIsCommercial(affiliate);
	};
	
	service.anySubmittedUsageReports = function(affiliate) {
		return _.some(affiliate.commercialUsages, function(usageReport) {
			return !ApprovalStateUtils.isWaitingForApplicant(usageReport.approvalState) && !usageReport.effectiveTo;
		});
	};

	
	
	service.retractUsageReport = function(commercialUsageReport) {
		var modalInstance = $modal.open({
			templateUrl: 'views/user/retractUsageReportModal.html',
			controller: 'RetractUsageReportController',
			size:'sm',
			backdrop: 'static',
			resolve: {
				commercialUsageReport: function() {
					return commercialUsageReport;
				}
			}
		});
		
		modalInstance.result
		.then(function(result) {
			if(result.data && result.data.commercialUsageId) {
				$location.path('/usageReports/usageLog/'+ result.data.commercialUsageId);
			}
		});

	};
	
	return service;
}]);

