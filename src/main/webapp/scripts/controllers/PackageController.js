'use strict';

angular.module('MLDS').controller('PackageController', 
		['$scope', '$log', '$routeParams', '$location', '$modal', 'PackagesService',
		 function($scope, $log, $routeParams, $location, $modal, PackagesService) {
	
	if ($routeParams && $routeParams.packageId) {
		PackagesService.get({releasePackageId: parseInt($routeParams.packageId, 10)})
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