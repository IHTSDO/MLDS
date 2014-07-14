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
                    releasePackage: function() {return releasePackage;}
                  }
                });
        };

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
        		if (packageEntity.releaseVersions[i].publishedAt) {
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

