'use strict';

angular.module('MLDS').controller('PackageController', 
		['$scope', '$log', '$routeParams', '$location', 'PackagesService',
		 function($scope, $log, $routeParams, $location, PackagesService) {
	
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
	
}]);