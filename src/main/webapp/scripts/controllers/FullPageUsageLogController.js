'use strict';

angular.module('MLDS').controller('FullPageUsageLogController', ['$scope', '$log', 'CountryService', 'CommercialUsageService', '$routeParams', 'Events', '$q', '$window', 'Session',
                                                 		function($scope, $log, CountryService, CommercialUsageService, $routeParams, Events, $q, $window, Session) {

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
	
	$scope.goBackToPrevious = function() {
		$window.history.back();	
	};
	
}]);