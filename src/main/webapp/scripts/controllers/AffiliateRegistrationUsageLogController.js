'use strict';

mldsApp.controller('AffiliateRegistrationUsageLogController',
        [ '$scope', '$log', 'CountryService', 'LicenseeService', 'Events', '$q', 'CommercialUsageService', 'UserRegistrationService',
          function ($scope, $log, CountryService, LicenseeService, Events, $q, CommercialUsageService, UserRegistrationService) {

        	var usageReportDefer = $q.defer();

        	// For use by EmbeddableUsageLogController
        	$scope.usageLogCanSubmit = false;
        	$scope.usageReportReady = usageReportDefer.promise;
        	
        	loadApplicationUsageReport();
        	
        	function loadApplicationUsageReport() {
        		CountryService.ready.then(function() {
        			var queryPromise =  UserRegistrationService.getApplication();
            		
            		queryPromise.success(function(data) {
            			// FIXME MB hack alert - updating the current pointer from controller load
            			var usageReport = data.commercialUsage;
            			if (usageReport) {
            				CommercialUsageService.currentCommercialUsageReport = usageReport;
            				usageReportDefer.resolve(usageReport);
            			} else {
            				$log.log('Failed to get usage report from current application', message)
            				usageReportDefer.reject(message);            				
            			}
            		})
            		["catch"](function(message) {
    					//FIXME
    					$log.log('Failed to get current user application', message);
    					usageReportDefer.reject(message);
    				});
        		})
				["catch"](function(message) {
					//FIXME
					$log.log('Failed to get countries', message);
					usageReportDefer.reject(message);
				});
        	};
     }
]);