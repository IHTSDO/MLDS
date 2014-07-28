'use strict';

angular.module('MLDS').controller('PackageManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location', 'PackageUtilsService',
    function ($scope, $log, $modal, PackagesService, $location, PackageUtilsService) {
			
		$scope.utils = PackageUtilsService;
		
		$scope.showAllMembers = false;
		
		//FIXME replace with a different mechanism
		function reloadPackages() {
			$scope.packages = PackagesService.query();
		}
		
		function extractPackages() {
			var packages = $scope.packages;
			
			var memberFiltered = _.chain(packages).filter(function(p){ return $scope.showAllMembers || PackageUtilsService.isReleasePackageMatchingMember(p); })
			$log.log('loading packages', packages, memberFiltered.value());
			
			$scope.onlinePackages = memberFiltered
				.filter(PackageUtilsService.isPackagePublished)
				.sortBy(PackageUtilsService.getLatestPublishedDate)
				.value();
			$scope.offinePackages = memberFiltered.reject(PackageUtilsService.isPackagePublished).sortBy(PackageUtilsService.getLatestPublishedDate).value();
			
			$log.log($scope.onlinePackages);
		}
		
		reloadPackages();
		
		$scope.$watch('showAllMembers', extractPackages);
		$scope.$watch('packages', extractPackages);
		
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
        
        $scope.isEditableReleasePackage = function(p) {
        	PackageUtilsService.isEditableReleasePackage(p);
        };
    }]);

