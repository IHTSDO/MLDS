'use strict';

angular.module('MLDS').controller('PackageController', 
		['$scope', '$log', '$routeParams', '$location', '$modal', 'PackagesService',
		 function($scope, $log, $routeParams, $location, $modal, PackagesService) {
	
	var getPackage = function(packageId) {
		var packages = PackagesService.query();
		for(var i = 0; i < packages.length; i++) {
			if (packages[i].releasePackageId == packageId) {
				$log.log('package found!');
				return packages[i];
			};
		};
		
		$log.log('package not found!');
		$location.path('/packageManagement');
	};
	
	if ($routeParams && $routeParams.packageId) {
		$scope.packageEntity = getPackage($routeParams.packageId);
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
                releasePackage: function() {return $scope.packageEntity;}
              }
            });
    };

    $scope.goToReleaseManagement = function() {
    	$location.path('/packageManagement');
    };
}]);