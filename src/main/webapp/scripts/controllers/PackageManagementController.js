'use strict';

angular.module('MLDS').controller('PackageManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location', 'PackageUtilsService',
    function ($scope, $log, $modal, PackagesService, $location, PackageUtilsService) {
			
		$scope.utils = PackageUtilsService;
		
		$scope.showAllMembers = false;
		$scope.packages = [];
		
		function reloadPackages() {
			$scope.packages = PackagesService.query();
			$scope.packages.$promise.then(extractPackages);
		}
		
		function extractPackages() {
			var packages = $scope.packages;
			
			var memberFiltered = _.chain(packages).filter(function(p){ return $scope.showAllMembers || PackageUtilsService.isReleasePackageMatchingMember(p); })
			
			$scope.onlinePackages = memberFiltered
				.filter(PackageUtilsService.isPackagePublished)
				.sortBy(PackageUtilsService.getLatestPublishedDate)
				.value();
			$scope.offinePackages = memberFiltered
				.reject(PackageUtilsService.isPackagePublished)
				.sortBy('createAt')
				.value();
		}
		
		$scope.$watch('showAllMembers', extractPackages);
		
		reloadPackages();
		
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
        	$location.path('/packageManagement/package/'+encodeURIComponent(packageEntity.releasePackageId));
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
        
        $scope.isEditableReleasePackage = function(p) {
        	PackageUtilsService.isEditableReleasePackage(p);
        };
    }]);

