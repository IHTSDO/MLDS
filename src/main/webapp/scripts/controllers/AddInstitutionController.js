'use strict';

angular.module('MLDS').controller('AddInstitutionController', ['$scope', '$modalInstance', 'country', '$timeout', '$log',
                                                       	function($scope, $modalInstance, country, $timeout, $log) {
	$scope.country = country;
	$scope.alerts = [];
	$scope.institution = {};
	$scope.institution.startDate = new Date();
	
	//TODO: AC rename(if needed) and fill in guts to submit new institution
	$scope.add = function(){
		$scope.submitting = true;
		
		$timeout(function() {
			$modalInstance.dismiss('cancel');
		}, 1000);
	};
	
	//TODO: example of adding alerts/errors to modal to be moved into 'add' function
	$scope.addFail = function(){
		$scope.submitting = true;
		
		$timeout(function() {
			$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
			$scope.submitting = false;
		}, 2000);
	};
	
	$scope.closeAlert = function(index) {
	    $scope.alerts.splice(index, 1);
	  };
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};

	$scope.open = function($event) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.opened = !$scope.opened;
	};

	$scope.dateOptions = {
		formatYear: 'yy',
		startingDay: 1,
		format: 'yyyy/MM/dd'
	};

}]);