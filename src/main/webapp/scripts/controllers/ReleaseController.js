'use strict';

angular.module('MLDS').controller('ReleaseController',
		['$scope', '$log', '$routeParams', '$location', '$modal', 'PackagesService', 'ReleaseFilesService', 'PackageUtilsService', 'ReleasePackageService',
		 function($scope, $log, $routeParams, $location, $modal, PackagesService, ReleaseFilesService, PackageUtilsService, ReleasePackageService) {

	$scope.versions = {
			online: [],
			offline: [],
			alphabeta: []
		};

	$scope.packageEntity = {releaseVersions:[]};

	$scope.updateVersionsLists = PackageUtilsService.updateVersionsLists;

	$scope.$watch('packageEntity', function(newValue, oldValue) {
		$scope.versions = $scope.updateVersionsLists(newValue);
	});


	var releasePackageId = $routeParams.packageId && parseInt($routeParams.packageId, 10);
	//$log.log('releasePackageId', releasePackageId);
	var loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				if (PackageUtilsService.isReleasePackageInactive(result)) {
					$log.info('Selected ReleasePackage is inactive');
					$scope.goToReleaseManagement();
				}

				$scope.packageEntity = result;
				$scope.isEditableReleasePackage = PackageUtilsService.isEditableReleasePackage(result);
				$scope.isRemovableReleasePackage = PackageUtilsService.isRemovableReleasePackage(result);
				})
					["catch"](function(message) {
						//FIXME how to handle errors + not present
						$log.error('ReleasePackage not found');
						$scope.goToReleaseManagement();
					});
		} else {
			$scope.goToReleaseManagement();
		};
	};

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
    debugger;
      modalInstance.result.then(loadReleasePackage);
	};

	$scope.editReleaseVersion = function editReleaseVersion(selectedReleaseVersion) {
        var modalInstance = $modal.open({
            templateUrl: 'views/admin/addEditReleaseVersionModal.html', // FM
            controller: 'AddEditReleaseVersionModalController', // FM
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
              releasePackage: function() {
              	return angular.copy($scope.packageEntity);
              },
              releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
          });
      modalInstance.result.then(loadReleasePackage);
	};

    $scope.editReleasePackage = function() {
        var modalInstance = $modal.open({
              templateUrl: 'views/admin/editReleaseModal.html',
              controller: 'EditReleaseModalController',
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

    $scope.removeReleasePackage = function() {
        var modalInstance = $modal.open({
              templateUrl: 'views/admin/removeReleaseModal.html',
              controller: 'RemoveReleaseModalController',
              scope: $scope,
              size: 'sm',
              backdrop: 'static',
              resolve: {
                releasePackage: function() {
                	// FIXME not sure about copy - needed to support modal cancel or network failure
                	return angular.copy($scope.packageEntity);
                }
              }
            });
        modalInstance.result.then(function() {
        	$scope.goToReleaseManagement();
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

    $scope.editReleaseFile = function editReleaseFile(releaseVersion, releaseFile) {
        var modalInstance = $modal.open({
            templateUrl: 'views/admin/editReleaseFileModal.html',
            controller: 'EditReleaseFileModalController',
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
                releasePackage: function() { return angular.copy($scope.packageEntity); },
                releaseFile: function() { return angular.copy(releaseFile); },
                releaseVersion: function() { return angular.copy(releaseVersion);},
            }
        });
        modalInstance.result.then(loadReleasePackage);
    };

    $scope.deleteVersionModal = function deleteVersionModal(selectedReleaseVersion) {
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
  	      	size: 'lg',
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


    $scope.takeAlphaBetaModal = function takeAlphaBetaModal(selectedReleaseVersion) {
        	var modalInstance = $modal.open({
      	      	templateUrl: 'views/admin/takeAlphaBetaModal.html',
      	      	controller: 'TakeAlphaBetaModalController',
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




    $scope.updateLicense = function() {
    	$log.log('Update License');
    	var modalInstance = $modal.open({
  	      	templateUrl: 'views/admin/releasePackageLicenseModal.html',
  	      	controller: 'ReleasePackageLicenseController',
  	      	scope: $scope,
  	      	size: 'lg',
  	      	backdrop: 'static',
  	      	resolve: {
              releasePackage: function() { return angular.copy($scope.packageEntity); }
            }
  	    });
    	modalInstance.result.then();
    };

    $scope.viewLicense = function() {
		ReleasePackageService.getReleaseLicense($scope.packageEntity.releasePackageId);
	};

    $scope.goToReleaseManagement = function() {
    	$location.path('/releaseManagement');
    };
}]);
