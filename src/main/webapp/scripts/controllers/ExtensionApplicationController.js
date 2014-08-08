'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', 
		['$scope', '$location', '$log', '$routeParams', 'UserRegistrationService', 'UserAffiliateService', 'ApplicationUtilsService',
           	function($scope, $location, $log, $routeParams, UserRegistrationService, UserAffiliateService, ApplicationUtilsService) {
	
	var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
	
	$scope.extensionForm = {};
	$scope.readOnly = false;
	$scope.isApplicationWaitingForApplicant = false;
	$scope.isApplicationRejected = false;
	
	UserRegistrationService.getApplicationById(applicationId)
		.then(function(result) {
			$scope.extensionForm = result.data;
			$scope.readOnly = !ApplicationUtilsService.isApplicationWaitingForApplicant(result.data);
			$scope.isApplicationWaitingForApplicant = ApplicationUtilsService.isApplicationWaitingForApplicant(result.data);
			$scope.isApplicationApproved = ApplicationUtilsService.isApplicationApproved(result.data);
			$scope.isApplicationRejected = ApplicationUtilsService.isApplicationRejected(result.data);
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