'use strict';

mldsApp.controller('AdminDashboardController',
        [ '$scope', '$log', '$location', 'UserRegistrationService', 'DomainBlacklistService', 'PackagesService', 
          function ($scope, $log, $location, UserRegistrationService, DomainBlacklistService, PackagesService) {
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
        	
        	
        	$scope.domainBlacklist = {};
        	function getDomainBlacklist() {
        		var queryPromise =  DomainBlacklistService.getDomainBlacklist();
        		
        		queryPromise.success(function(data) {
        			$scope.domainBlacklist = data;
        		});
        	}
        	getDomainBlacklist();
        	
        	$scope.domainform = {};
        	$scope.domainform.submit = function newDomainSubmit() {
    			var queryPromise = DomainBlacklistService.addDomain($scope.domainform.name);
        		
        		queryPromise.then(function(){
        			$scope.domainform.name = '';
        			getDomainBlacklist();
        		});
        	};
        	
        	$scope.removeDomain = function(domain) {
    			var queryPromise = DomainBlacklistService.removeDomain(domain);
        		
        		queryPromise.then(function(){
        			getDomainBlacklist();
        		});
        	};
        	
        	
        }
    ]);