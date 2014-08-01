'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', ['$scope', '$location', '$log', '$routeParams', 'UserRegistrationService',
                                                       	function($scope, $location, $log, $routeParams, UserRegistrationService) {
	
	var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
	$log.info('ExtensionApplicationController', applicationId);
	
	$scope.extensionForm = {};
	
	var loadApplication = function loadApplication() {
		if (applicationId) {
			UserRegistrationService.getApplicationById(applicationId)
				.then(function(result) {
					$log.log(result);
					if(result.data) {
						$scope.extensionForm = result.data;
					}
					
					})
					["catch"](function(message) {
						//FIXME how to handle errors + not present 
						$log.log('ReleasePackage not found');
						$location.path('/viewPackages');
					});
		} else {
			$location.path('/viewPackages');
		};
	};
	
	loadApplication();
	
	$scope.submit = function(){
		UserRegistrationService.saveApplication($scope.extensionForm, applicationId)
			.then(function(result) {
				$log.log('extensionForm.submit.result', result);
			});
	};
		

}]);