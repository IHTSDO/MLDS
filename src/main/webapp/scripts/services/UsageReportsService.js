 'use strict';

angular.module('MLDS')
.factory('UsageReportsService', [ '$location', '$log','$http', '$modal', 'AffiliateService', 'UsageReportStateUtils',
                                    function($location, $log, $http, $modal, AffiliateService, UsageReportStateUtils){

	var service = {};

	service.usageReportCountries = function(usageReport) {
		return usageReport.countries.length;
	};

	service.usageReportHospitals = function(usageReport) {
		return usageReport.entries.length;
	};

	service.usageReportPractices = function(usageReport) {
		return usageReport.countries.reduce(function(total, count) {
			return total + (count.snomedPractices || 0);
		}, 0);
	};

	service.isInvoiceSent = function(usageReport) {
		return UsageReportStateUtils.isInvoiceSent(usageReport.state);
	};

	service.isPendingInvoice = function(usageReport) {
		return UsageReportStateUtils.isPendingInvoice(usageReport.state);
	};

	service.isSubmitted = function(usageReport) {
		return UsageReportStateUtils.isSubmitted(usageReport.state);
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
			return !UsageReportStateUtils.isWaitingForApplicant(usageReport.state) && !usageReport.effectiveTo;
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

/*MLDS-985 Review Usage Reports*/
    service.getReviewUsageReport = function(){
    return $http.get('/api/reviewUsageReports');
    }
/*MLDS-985 Review Usage Reports*/

	return service;
}]);

