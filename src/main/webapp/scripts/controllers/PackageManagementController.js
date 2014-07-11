'use strict';

angular.module('MLDS').controller('PackageManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location',
    function ($scope, $log, $modal, PackagesService, $location) {
			
		$scope.packages = PackagesService.query();
        
		// FIXME: AC Using Example to show both modals
        $scope.takePackageOffline =  $scope.makePackageOnline = function() {
        	$log.log('button clicked');
        	
        	var modalInstance = $modal.open({
        	      templateUrl: 'views/admin/takeOfflineModal.html',
        	      controller: 'TakeOfflineModalController',
        	      scope: $scope,
        	      size: 'sm',
        	      windowClass: 'debugTest'
        	    });
        };
        
        
        $scope.isPackagePublished = function(packageEntity) {
        	for(var i = 0; i < packageEntity.versions.length; i++) {
        		if (packageEntity.versions[i].publishedAt) {
        			return true;
        		};
        	};
        	return false;
        };
        
        $scope.isLatestPublishedVersion = function(version, versions) {
        	for(var i = 0; i < versions.length; i++) {
        		if (versions[i].publishedAt && version.publishedAt &&
        				(versions[i].publishedAt > version.publishedAt)) {
        			return false;
        		};
        	};
        	return true;
        };
        
    }]);

