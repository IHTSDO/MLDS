'use strict';

mldsApp.controller('AffiliateRegistrationUsageLogController',
        [ '$scope', '$log', 'CountryService', 'LicenseeService', 'Events', '$q', 'CommercialUsageService',
          function ($scope, $log, CountryService, LicenseeService, Events, $q, CommercialUsageService) {

        	var usageReportDefer = $q.defer();

        	// For use by EmbeddableUsageLogController
        	$scope.usageLogCanSubmit = false;
        	$scope.usageReportReady = usageReportDefer.promise;
        	
        	loadMostRecentUsageReport();
        	
        	function loadMostRecentUsageReport() {
        		CountryService.ready.then(function() {
        			LicenseeService.myLicensees()
        				.then(function(result) {
        					var licensees = result.data;
        					//FIXME making a lot of assumptions here about licensees...
        					//FIXME what if userReport has already been submitted?
        					if (licensees && licensees.length > 0 && licensees[0].commercialUsages && licensees[0].commercialUsages.length > 0) {
        						var usageReport = licensees[0].commercialUsages.reduce(function(latestUsageReport, usageReport) {
        							if (!latestUsageReport || new Date(usageReport.startDate) < new Date(latestUsageReport.startDate)) {
        								return latestUsageReport;
        							} else {
        								return usageReport;
        							}
        						});
//        						var usageReport = licensees[0].commercialUsages[0];
        						// FIXME MB hack alert - updating the current pointer from controller load
        						CommercialUsageService.currentCommercialUsageReport = usageReport;
        						usageReportDefer.resolve(usageReport);
        					} else {
        						$log.log('User does not have any usage reports yet...');
        						usageReportDefer.reject('User does not have any usage reports yet...');
        					}
        				})
    					["catch"](function(message) {
    						//FIXME
    						$log.log('Failed to get initial usage log by param');
    						usageReportDefer.reject(message);
    					});
        				
        		});
        	}        	
        }
]);