'use strict';

angular.module('MLDS').controller('SubmitUsageReportController', ['$scope', '$modalInstance',  '$log', '$location', 'CommercialUsageService', 'commercialUsageReport', 'usageByCountryList', 'UserAffiliateService', 'Session', 
                                                       	function($scope, $modalInstance, $log, $location, CommercialUsageService, commercialUsageReport, usageByCountryList, UserAffiliateService, Session) {
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.commercialUsageReport = commercialUsageReport;
	$scope.usageByCountryList = usageByCountryList;
	
	$scope.submit = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.submitUsageReport($scope.commercialUsageReport)
			.then(function(result) {
				if (Session.isAdmin()) {
					$location.path('/affiliateManagement/' + commercialUsageReport.affiliate.affiliateId);
				} else {
					UserAffiliateService.refreshAffiliate();
					$location.path('/dashboard');
				}
				
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

	
	$scope.open = function($event, datepickerName) {
		$event.preventDefault();
		$event.stopPropagation();
	};


}]);