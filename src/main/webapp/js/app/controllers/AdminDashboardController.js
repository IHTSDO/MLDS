'use strict';

angular.module('MLDS')
    .controller('AdminDashboardController',
        [ '$scope', '$log', 'UserRegistrationService', function ($scope, $log, UserRegistrationService) {
        	
        	console.log('Dashboard Controller');
        	
        	$scope.applications = {};
        	
        	
        	function getApplications() {
        		var queryPromise =  UserRegistrationService.getApplications();
        		
        		queryPromise.success(function(data) {
        			$scope.applications = data;
        		});
        	}
        	
        	getApplications();
        	
        	
        	$scope.approveApplication = function(username){
        		console.log('approveApplication for ', username);
        		var queryPromise = UserRegistrationService.approveApplication(username);
        		
        		queryPromise.then(function(){
        			getApplications();
        		});
        	};
        }
    ]);