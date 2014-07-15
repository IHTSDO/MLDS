'use strict';

angular.module('MLDS').controller('PackageController', 
		['$scope', '$log', '$routeParams', '$location', '$modal', 'PackagesService',
		 function($scope, $log, $routeParams, $location, $modal, PackagesService) {

	$scope.versions = {
			online: [],
			offline: []
		};
	$scope.packageEntity = {};
	
	$scope.$watch('packageEntity', function(newValue, oldValue) {
		$log.log('watch updated!');
		$scope.versions = updateVersionsLists(newValue);
		
	});
	
	var updateVersionsLists = function updateVersionsLists(packgeEntity) {
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
	}
	
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

    $scope.goToReleaseManagement = function() {
    	$location.path('/packageManagement');
    };
}]);