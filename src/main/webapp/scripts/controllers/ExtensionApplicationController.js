'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', ['$scope', '$location', '$log', 'application', 'UserRegistrationService',
                                                       	function($scope, $location, $log, application, UserRegistrationService) {
	
	$scope.extensionForm = application;
	
	$scope.submit = function(){
		$scope.etensionForm.approvalState = 'SUBMITTED';
		
		UserRegistrationService.updateApplication($scope.extensionForm)
			.then(function(result) {
				$log.log('extensionForm.submit.result', result);
			});
	};
		

}]);