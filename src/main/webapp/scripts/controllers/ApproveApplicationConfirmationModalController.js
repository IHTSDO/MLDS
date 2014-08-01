'use strict';

angular.module('MLDS').controller('ApproveApplicationConfirmationModalController', 
		['$scope', '$log', '$modalInstance', 'application',
		 function($scope, $log,  $modalInstance, application) {
	
	$scope.application = application;
	
}]);