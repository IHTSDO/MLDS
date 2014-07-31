'use strict';

mldsApp.controller('AdminDashboardController',
        [ '$scope', '$log', '$location', 'UserRegistrationService','PackagesService', 
          function ($scope, $log, $location, UserRegistrationService, PackagesService) {
        	$log.log('packages', PackagesService.query);
        	
        	$log.log('Dashboard Controller');
        	
        	$scope.applications = {};
        	
        	function getApplications() {
        		var queryPromise =  UserRegistrationService.getApplications();
        		
        		queryPromise.success(function(data) {
        			$scope.applications = data;
        		});
        	}
        	
        	getApplications();
        	
        	$scope.approveApplication = function(username){
        		var queryPromise = UserRegistrationService.approveApplication(username);
        		
        		queryPromise.then(function(){
        			getApplications();
        		});
        	};
        	
        }
    ]);