'use strict';

angular.module('MLDS')
    .controller('ViewPackagesController', 
    		['$scope', '$log', 'PackagesService', 'PackageUtilsService', '$location',
           function ($scope, $log, PackagesService, PackageUtilsService, $location) {
			
	$scope.utils = PackageUtilsService;
	$scope.releasePackages = PackagesService.query();
	$log.log(PackagesService.query());
	
	$scope.goToViewPackagePage = function goToViewPackagePage(releasePackageId) {
		$location.path('/viewPackage/'+ releasePackageId);
	};

}]);
