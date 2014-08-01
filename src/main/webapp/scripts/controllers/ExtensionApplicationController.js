'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', ['$scope', '$location', '$log', 'application', 'UserRegistrationService',
                                                       	function($scope, $location, $log, application, UserRegistrationService) {
	
	$scope.extensionForm = application;
	
	$scope.submit = function(){
		UserRegistrationService.submitApplication($scope.extensionForm, $scope.extensionForm.applicationId)
			.then(function(result) {
				$log.log('extensionForm.submit.result', result);
			});
	};
		

}]);