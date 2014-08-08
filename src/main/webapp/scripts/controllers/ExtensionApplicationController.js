'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', 
		['$scope', '$location', '$log', '$routeParams', 'UserRegistrationService', 'UserAffiliateService',
           	function($scope, $location, $log, $routeParams, UserRegistrationService, UserAffiliateService) {
	
	var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
	
	$scope.extensionForm = {};
	$scope.readOnly = false;
	
	
	UserRegistrationService.getApplicationById(applicationId)
		.then(function(result) {
			$scope.extensionForm = result.data;
			$scope.readOnly = result.data.approvalState == 'SUBMITTED' || result.data.approvalState == 'RESUBMITTED';
			//$log.log('result', result.data);
		})["catch"](function(message) {
			//FIXME how to handle errors + not present 
			$log.log('application not found');
			$location.path('/viewPackages');
		});
	
	
	$scope.submit = function(){
		$scope.extensionForm.approvalState = 'SUBMITTED';
		
		UserRegistrationService.updateApplication($scope.extensionForm)
			.then(function(result) {
				$log.log('extensionForm.submit.result', result);
				UserAffiliateService.refreshAffiliate();
				// FIXME where do we send the user?
				$location.path('/dashboard');
			});
	};
		

}]);