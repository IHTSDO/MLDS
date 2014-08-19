'use strict';

angular.module('MLDS')
.factory('UsageReportsService', [ '$location', '$log', '$modal', 'AffiliateService',
                                    function($location, $log, $modal, AffiliateService){

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
	
	service.affiliateIsCommercial = function(affiliate) {
		return AffiliateService.affiliateIsCommercial(affiliate);
	};
	
	service.anySubmittedUsageReports = function(affiliate) {
		return _.some(affiliate.commercialUsages, function(usageReport) {
			return usageReport.approvalState !== 'NOT_SUBMITTED';
		});
	};

	
	return service;
}]);

