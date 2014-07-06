'use strict';

angular.module('MLDS').controller('FullPageUsageLogController', ['$scope', '$log', 'CountryService', 'CommercialUsageService', '$routeParams', 'Events', '$q', 
                                                 		function($scope, $log, CountryService, CommercialUsageService, $routeParams, Events, $q) {

	var usageReportDefer = $q.defer();

	// For use by EmbeddableUsageLogController
	$scope.usageLogCanSubmit = true;
	$scope.usageReportReady = usageReportDefer.promise;
	
	loadUsageReportFromRoute();
	
	function loadUsageReportFromRoute() {
		CountryService.ready.then(function() {
			if ($routeParams && $routeParams.usageReportId) {
				CommercialUsageService.getUsageReport($routeParams.usageReportId)
					.then(function(result) {
						$log.log('updating usage report');
						$log.log(result.data);
						usageReportDefer.resolve(result.data);
					})
					["catch"](function(message) {
						//FIXME
						$log.log('Failed to get initial usage log by param');
						usageReportDefer.reject(message);
					});
			} else {
				$log.log('Missing usage report id');
				usageReportDefer.reject('Missing usage report id');
			}
		});
	}
	
}]);