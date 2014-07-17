'use strict';

angular.module('MLDS').controller('ApproveApplicationConfirmationModalController', 
		['$scope', '$log', '$modalInstance', 'UserRegistrationService', 'application',
		 function($scope, $log,  $modalInstance, UserRegistrationService, application) {
	
	$scope.application = application;
	
}]);