'use strict';

angular.module('MLDS').controller('PackageManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location',
    function ($scope, $log, $modal, PackagesService, $location) {
			
		$scope.packages = PackagesService.query();
        
		$scope.isPackagePublished = function isPackagePublished(packageEntity) {
        	for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
        		if (packageEntity.releaseVersions[i].online) {
        			return true;
        		}
        	}
        	return false;
        };
        
        
        $scope.isLatestPublishedVersion = function isLatestPublishedVersion(version, versions) {
        	for(var i = 0; i < versions.length; i++) {
        		if (versions[i].publishedAt && version.publishedAt &&
        				(versions[i].publishedAt > version.publishedAt)) {
        			return false;
        		};
        	};
        	return true;
        };
        
        $scope.getLatestPublishedDate = function getLatestPublishedDate(packageEntity) { 
    		var latestPublishDate = 0; 
    		for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
    			if (i == 0) {
    				latestPublishDate = packageEntity.releaseVersions[i].publishedAt;
    			} else if (packageEntity.releaseVersions[i].publishedAt > latestPublishDate ) {
    				latestPublishDate = packageEntity.releaseVersions[i].publishedAt;
    			};
    		};
    		return latestPublishDate;
        };
		
		//FIXME replace with a different mechanism
		function reloadPackages() {
			$scope.packages = PackagesService.query();	
		}
		
		$scope.addReleasePackage = function() {
			var modalInstance = $modal.open({
				templateUrl: 'views/admin/addPackageModal.html',
				controller: 'AddPackageModalController',
				size:'lg',
				backdrop: 'static',
				resolve: {
				}
			});
		};

        $scope.editReleasePackage = function editReleasePackage(releasePackage) {
            var modalInstance = $modal.open({
                  templateUrl: 'views/admin/editPackageModal.html',
                  controller: 'EditPackageModalController',
                  scope: $scope,
                  size: 'lg',
                  backdrop: 'static',
                  resolve: {
                    releasePackage: function() {
                    	//FIXME not sure about copy - needed to support modal cancel or network failure
                    	return angular.copy(releasePackage);
                    }
                  }
                });
            modalInstance.result.then(function(result) {
            	reloadPackages();
            });
        };
        
        $scope.goToPackage = function(packageEntity) {
        	$location.path('/package/'+encodeURIComponent(packageEntity.releasePackageId));
        };
        
        $scope.deleteReleasePackage = function(releasePackage) {
            var modalInstance = $modal.open({
                templateUrl: 'views/admin/deletePackageModal.html',
                controller: 'DeletePackageModalController',
                scope: $scope,
                size: 'sm',
                backdrop: 'static',
                resolve: {
                  releasePackage: function() {
                  	return releasePackage;
                  }
                }
              });
            modalInstance.result.then(function(result) {
            	reloadPackages();
            });
        };
    }]);

