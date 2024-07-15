'use strict';

angular.module('MLDS').controller('ReleaseController',
		['$scope', 'ServicesUtils','PackagesService', 'ReleaseFilesService', 'PackageUtilsService', 'ReleasePackageService','ReleaseVersionsService' ,
		 function($scope, ServicesUtils,PackagesService, ReleaseFilesService, PackageUtilsService, ReleasePackageService, ReleaseVersionsService) {

	$scope.versions = {
			online: [],
			offline: [],
			alphabeta: [],
			archive: []
		};

	$scope.packageEntity = {releaseVersions:[]};

	$scope.updateVersionsLists = PackageUtilsService.updateVersionsLists;

	$scope.$watch('packageEntity', function(newValue, oldValue) {
		$scope.versions = $scope.updateVersionsLists(newValue);
	});


	let releasePackageId = ServicesUtils.$routeParams.packageId && parseInt(ServicesUtils.$routeParams.packageId, 10);
	let loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				if (PackageUtilsService.isReleasePackageInactive(result)) {
					ServicesUtils.$log.info('Selected ReleasePackage is inactive');
					$scope.goToReleaseManagement();
				}

				$scope.packageEntity = result;
				$scope.isEditableReleasePackage = PackageUtilsService.isEditableReleasePackage(result);
				$scope.isRemovableReleasePackage = PackageUtilsService.isRemovableReleasePackage(result);
				})
					["catch"](function(message) {
						ServicesUtils.$log.error('ReleasePackage not found');
						$scope.goToReleaseManagement();
					});
		} else {
			$scope.goToReleaseManagement();
		};
	};

	loadReleasePackage();

	$scope.addReleaseVersion = function addReleaseVersion() {
        let modalInstance = ServicesUtils.$modal.open({
            templateUrl: 'views/admin/addEditReleaseVersionModal.html',
            controller: 'AddEditReleaseVersionModalController',
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
              releasePackage: function() {
              	return angular.copy($scope.packageEntity);
              },
              releaseVersion: function() { return {}; }
            }
          });
      modalInstance.result.then(loadReleasePackage);
	};

	$scope.editReleaseVersion = function editReleaseVersion(selectedReleaseVersion) {
        let modalInstance = ServicesUtils.$modal.open({
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
        let modalInstance = ServicesUtils.$modal.open({
              templateUrl: 'views/admin/editReleaseModal.html',
              controller: 'EditReleaseModalController',
              scope: $scope,
              size: 'lg',
              backdrop: 'static',
              resolve: {
                releasePackage: function() {
                	return angular.copy($scope.packageEntity);
                }
              }
            });
        modalInstance.result.then(function(updatedReleasePackage) {
        	$scope.packageEntity = updatedReleasePackage;
        });
    };

    $scope.removeReleasePackage = function() {
        let modalInstance = ServicesUtils.$modal.open({
              templateUrl: 'views/admin/removeReleaseModal.html',
              controller: 'RemoveReleaseModalController',
              scope: $scope,
              size: 'sm',
              backdrop: 'static',
              resolve: {
                releasePackage: function() {
                	return angular.copy($scope.packageEntity);
                }
              }
            });
        modalInstance.result.then(function() {
        	$scope.goToReleaseManagement();
        });
    };

    $scope.addReleaseFile = function addReleaseFile(selectedReleaseVersion) {
        let modalInstance = ServicesUtils.$modal.open({
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
        let modalInstance = ServicesUtils.$modal.open({
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

    $scope.deleteVersionModal = function(selectedReleaseVersion) {
        determineDependencyPresence(selectedReleaseVersion).then(function(isDependencyPresent){
        let modalTemplateUrl = (isDependencyPresent === 'true')
            ? 'views/admin/deleteVersionModalDependent.html'
            : 'views/admin/deleteVersionModal.html';
            let modalInstance = ServicesUtils.$modal.open({
            templateUrl: modalTemplateUrl,
            controller: 'DeleteVersionModalController',
            scope: $scope,
            size: 'lg',
            backdrop: 'static',
            resolve: {
                releasePackage: function() { return angular.copy($scope.packageEntity); },
                releaseVersion: function() { return angular.copy(selectedReleaseVersion); }
            }
        });
        modalInstance.result.then(loadReleasePackage);
        }).catch(function(error){
        console.error('Error checking dependency:', error);
        });
    };


    function determineDependencyPresence(selectedReleaseVersion) {
        let releaseVersionId = selectedReleaseVersion.releaseVersionId;
                return ServicesUtils.$http.get('api/checkVersionDependency/' + releaseVersionId)
                    .then(function(response) {
                        return response.data;
                    })
                    .catch(function(error) {
                        console.error('Error checking dependency:', error);
                        return false;
                    });
    }

    $scope.takeOnlineModal = function takeOnlineModal(selectedReleaseVersion) {
    	let modalInstance = ServicesUtils.$modal.open({
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
    	let modalInstance = ServicesUtils.$modal.open({
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
        	let modalInstance = ServicesUtils.$modal.open({
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
    	ServicesUtils.$log.log('Update License');
    	let modalInstance = ServicesUtils.$modal.open({
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
    	ServicesUtils.$location.path('/releaseManagement');
    };

    $scope.goToArchiveReleases = function() {
        ServicesUtils.$location.path('/archiveReleases');
    };

    $scope.moveToReleaseManagement = function(selectedReleaseVersion) {
         let releaseVersionId = selectedReleaseVersion.releaseVersionId;
         let archiveValue = !selectedReleaseVersion.archive;
         let requestData = archiveValue;
         ReleasePackageService.updateArchive(releaseVersionId, requestData)
         .then(loadReleasePackage);
    }
}]);
