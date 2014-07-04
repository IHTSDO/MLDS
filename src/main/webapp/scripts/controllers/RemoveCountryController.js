'use strict';

angular.module('MLDS').controller('RemoveCountryController', ['$scope', '$modalInstance',  '$log', '$location', 'count', 'usageReport', 'hospitalsCount', 'practicesCount', 'CommercialUsageService', 
                                                       	function($scope, $modalInstance, $log, $location, count, usageReport, hospitalsCount, practicesCount, CommercialUsageService) {
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.count = count;
	$scope.hospitalsCount = hospitalsCount;
	$scope.practicesCount = practicesCount;
	
	$scope.submit = function(){
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		CommercialUsageService.deleteUsageCount(usageReport, count)
			.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
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