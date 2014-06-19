'use strict';

angular.module('MLDS').controller('AddInstitutionController', ['$scope', '$modalInstance', 'country', '$timeout', '$log',
                                                       	function($scope, $modalInstance, country, $timeout, $log) {
	$scope.country = country;
	$scope.alerts = [];
	
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
	
	
	
	
	
	$scope.today = function() {
	    $scope.dt = new Date();
	  };
	  $scope.today();

	  $scope.clear = function () {
	    $scope.dt = null;
	  };

	  // Disable weekend selection
	  $scope.disabled = function(date, mode) {
	    return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
	  };

	  $scope.toggleMin = function() {
	    $scope.minDate = $scope.minDate ? null : new Date();
	  };
	  $scope.toggleMin();

	  $scope.open = function($event) {
	    $event.preventDefault();
	    $event.stopPropagation();

	    $scope.opened = true;
	  };

	  $scope.dateOptions = {
	    formatYear: 'yy',
	    startingDay: 1
	  };

	  $scope.initDate = new Date('2016-15-20');
	  $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
	  $scope.format = $scope.formats[0];
}]);