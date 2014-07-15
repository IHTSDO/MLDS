'use strict';

mldsApp.controller('PendingApplicationsController',
        [ '$scope', '$log', 'UserRegistrationService', 'DomainBlacklistService', 'PackagesService', 
          function ($scope, $log, UserRegistrationService, DomainBlacklistService, PackagesService) {

        	$scope.applications = {};
        	
        	function getApplications() {
        		var queryPromise =  UserRegistrationService.getApplications();
        		
        		queryPromise.success(function(data) {
        			$scope.applications = data;
        		});
        	}
        	
        	getApplications();

        	$scope.goToApplication = function(application) {
        		//FIXME set location
        	};
        }
]);
