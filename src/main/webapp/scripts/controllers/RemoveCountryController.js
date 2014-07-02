'use strict';

angular.module('MLDS').controller('RemoveCountryController', ['$scope', '$modalInstance',  '$log', '$location', 'country', 'hospitalsCount', 'practicesCount', 
                                                       	function($scope, $modalInstance, $log, $location, country, hospitalsCount, practicesCount) {
	$scope.alerts = [];
	
	$scope.country = country;
	$scope.hospitalsCount = hospitalsCount;
	$scope.practicesCount = practicesCount;
	
	$scope.submit = function(){
		$scope.submitting = true;
		
		$modalInstance.close($scope.country);
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