'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', 
		['$scope', '$location', '$log', '$modal', '$routeParams', 'UserRegistrationService', 'UserAffiliateService', 'ApplicationUtilsService', 'MemberService', 
           	function($scope, $location, $log, $modal, $routeParams, UserRegistrationService, UserAffiliateService, ApplicationUtilsService, MemberService) {
	
	var applicationId = $routeParams.applicationId && parseInt($routeParams.applicationId, 10);
	
	$scope.extensionForm = {};
	$scope.readOnly = false;
	$scope.isApplicationWaitingForApplicant = false;
	$scope.isApplicationRejected = false;
	
	$scope.viewLicense = function (memberKey) {
		MemberService.getMemberLicense(memberKey);
	};
	
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
			$location.path('/viewReleases');
		});
	
	
	$scope.submit = function(){
				
		if ($scope.extensionApplicationForm.$invalid) {
			$scope.extensionApplicationForm.attempted = true;
			return;
		};
		
		$scope.extensionForm.approvalState = 'SUBMITTED';
		
		UserRegistrationService.updateApplication($scope.extensionForm)
			.then(function(result) {
				$log.log('extensionForm.submit.result', result);
				UserAffiliateService.refreshAffiliate();
				// FIXME where do we send the user?
				$location.path('/dashboard');
			});
	};
		
	$scope.cancelApplication = function() {
		$log.info('Cancel Application');
		
		var modalInstance = $modal.open({
	      templateUrl: 'views/user/DeleteExtensionApplication.html',
	      size: 'sm'
	    });

	    modalInstance.result.then(function () {
	    	$log.info('Deleting Application: ', applicationId);
	    	UserRegistrationService.deleteApplication(applicationId)
	    		.then(function(result) {
	    			$log.info('Application was successfully removed');
	    			UserAffiliateService.refreshAffiliate();
	    			$location.path('/dashboard');
	    		}, function(error){
	    			$log.error('Application could not be deleted');
	    		});
	    		
	    }, function () {
	    });
		
	};

}]);