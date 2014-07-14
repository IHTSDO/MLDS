'use strict';

angular.module('MLDS').controller('PackageManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location',
    function ($scope, $log, $modal, PackagesService, $location) {
			
		$scope.packages = PackagesService.query();
        
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
            modalInstance.result.then(function(savedReleasePackageCopy) {
            	mergeReleasePackageCopyBack(savedReleasePackageCopy);
            	$log.log('edit complete', savedReleasePackageCopy);
            	$log.log('list', $scope.packages);
            });
        };

        function mergeReleasePackageCopyBack(releasePackageCopy) {
			for (var i = 0; i < $scope.packages.length; i++) {
				var releasePackage = $scope.packages[i];
				if (releasePackage.releasePackageId === releasePackageCopy.releasePackageId) {
					$scope.packages[i] = releasePackageCopy;
					return;
				}
			}
			//FIXME what should we do here?
			$log.log('Failed to merge ReleasePackageCopy back into the current releasePackages...')
        }
        
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
        	for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
        		if (packageEntity.releaseVersions[i].online) {
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
        
        $scope.goToPackage = function(packageEntity) {
        	$location.path('/package/'+encodeURIComponent(packageEntity.releasePackageId));
        };        
    }]);

