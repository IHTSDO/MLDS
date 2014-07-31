'use strict';

angular.module('MLDS').controller('BlocklistController',
		['$scope', '$log', 'DomainBlacklistService',
    function ($scope, $log, DomainBlacklistService) {
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
    }]);

