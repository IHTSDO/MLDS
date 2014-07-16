'use strict';

angular.module('MLDS').controller('PackageController', 
		['$scope', '$log', '$routeParams', '$location', '$modal', 'PackagesService', 'ReleaseFilesService',
		 function($scope, $log, $routeParams, $location, $modal, PackagesService, ReleaseFilesService) {

	$scope.versions = {
			online: [],
			offline: []
		};
	$scope.packageEntity = { releaseVersions:[]};
	
	$scope.$watch('packageEntity', function(newValue, oldValue) {
		$scope.versions = $scope.updateVersionsLists(newValue);
	});
	
	$scope.updateVersionsLists = function updateVersionsLists(packgeEntity) {
		var results = { online: [], offline: [] };
		var publishedOfflineVersions = [];
		var nonPublishedOfflineVersions = [];
		
		for(var i = 0; i < packgeEntity.releaseVersions.length; i++) {
			if (packgeEntity.releaseVersions[i].online) {
				results.online.push(packgeEntity.releaseVersions[i]);
			} else {
				if (packgeEntity.releaseVersions[i].publishedAt) {
					publishedOfflineVersions.push(packgeEntity.releaseVersions[i]);
				} else {
					nonPublishedOfflineVersions.push(packgeEntity.releaseVersions[i]);
				}
			}
		};
		
		results.online.sort(function(a,b){return new Date(b.publishedAt) - new Date(a.publishedAt);});
		publishedOfflineVersions.sort(function(a,b){return new Date(b.publishedAt) - new Date(a.publishedAt);});
		nonPublishedOfflineVersions.sort(function(a,b){return new Date(b.createdAt) - new Date(a.createdAt);});
		results.offline = publishedOfflineVersions.concat(nonPublishedOfflineVersions);
		
		return results;
	};
	
			
	var releasePackageId = $routeParams.packageId && parseInt($routeParams.packageId, 10);
	var loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				$scope.packageEntity = result;
				})
					["catch"](function(message) {
						//FIXME how to handle errors + not present
						$log.log('ReleasePackage not found');
						$location.path('/packageManagement');
					});
		} else {
			$location.path('/packageManagement');
		};
	}

	loadReleasePackage();
	
	$scope.addReleaseVersion = function addReleaseVersion() {
        var modalInstance = $modal.open({
            templateUrl: 'views/admin/addEditReleaseVersionModal.html',
            controller: 'AddEditReleaseVersionModalController',
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
              releasePackage: function() {
              	// FIXME not sure about copy - needed to support modal cancel or network failure
              	return angular.copy($scope.packageEntity);
              },
              releaseVersion: function() { return {}; }
            }
          });
      modalInstance.result.then(loadReleasePackage);
	}
	
	$scope.editReleaseVersion = function addReleaseVersion(selectedReleaseVersion) {
        var modalInstance = $modal.open({
            templateUrl: 'views/admin/addEditReleaseVersionModal.html', // FM
            controller: 'AddEditReleaseVersionModalController', // FM
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
              releasePackage: function() {
              	// FIXME not sure about copy - needed to support modal cancel or network failure
              	return angular.copy($scope.packageEntity);
              },
              releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
          });
      modalInstance.result.then(loadReleasePackage);
	};
	
    $scope.editReleasePackage = function() {
        var modalInstance = $modal.open({
              templateUrl: 'views/admin/editPackageModal.html',
              controller: 'EditPackageModalController',
              scope: $scope,
              size: 'lg',
              backdrop: 'static',
              resolve: {
                releasePackage: function() {
                	// FIXME not sure about copy - needed to support modal cancel or network failure
                	return angular.copy($scope.packageEntity);
                }
              }
            });
        modalInstance.result.then(function(updatedReleasePackage) {
        	$scope.packageEntity = updatedReleasePackage;
        });
    };
    
    $scope.addReleaseFile = function addReleaseFile(selectedReleaseVersion) {
        var modalInstance = $modal.open({
            templateUrl: 'views/admin/addReleaseFileModal.html', // FM
            controller: 'AddReleaseFileModalController', // FM
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
              releasePackage: function() { return angular.copy($scope.packageEntity); },
              releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
          });
      modalInstance.result.then(loadReleasePackage);
    };
    
    $scope.deleteReleaseFile = function deletePackageFile(releaseVersion, releaseFile) {
    	ReleaseFilesService['delete'](
    			{
    				releasePackageId: $scope.packageEntity.releasePackageId,
    				releaseVersionId: releaseVersion.releaseVersionId,
    				releaseFileId: releaseFile.releaseFileId})
			.$promise.then(loadReleasePackage);
    };

    $scope.deleteVersionModal = function takeOnlineModal(selectedReleaseVersion) {
    	var modalInstance = $modal.open({
  	      	templateUrl: 'views/admin/deleteVersionModal.html',
  	      	controller: 'DeleteVersionModalController',
  	      	scope: $scope,
  	      	size: 'sm',
  	      	backdrop: 'static',
  	      	resolve: {
              releasePackage: function() { return angular.copy($scope.packageEntity); },
              releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
  	    });
    	modalInstance.result.then(loadReleasePackage);
    };
    
    
    $scope.takeOnlineModal = function takeOnlineModal(selectedReleaseVersion) {
    	var modalInstance = $modal.open({
  	      	templateUrl: 'views/admin/takeOnlineModal.html',
  	      	controller: 'TakeOnlineModalController',
  	      	scope: $scope,
  	      	size: 'sm',
  	      	backdrop: 'static',
  	      	resolve: {
              releasePackage: function() { return angular.copy($scope.packageEntity); },
              releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
  	    });
    	modalInstance.result.then(loadReleasePackage);
    };
    
    $scope.takeOfflineModal = function takeOfflineModal(selectedReleaseVersion) {
    	var modalInstance = $modal.open({
  	      	templateUrl: 'views/admin/takeOfflineModal.html',
  	      	controller: 'TakeOfflineModalController',
  	      	scope: $scope,
  	      	size: 'sm',
  	      	backdrop: 'static',
  	      	resolve: {
              releasePackage: function() { return angular.copy($scope.packageEntity); },
              releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
  	    });
    	modalInstance.result.then(loadReleasePackage);
    };
    
    
    $scope.goToReleaseManagement = function() {
    	$location.path('/packageManagement');
    };
}]);