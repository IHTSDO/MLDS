'use strict';

angular.module('MLDS')
    .controller('ViewPackageController', 
    		['$scope', '$routeParams', 'PackagesService', 'PackageUtilsService', '$location', '$log',
          function($scope, $routeParams, PackagesService, PackageUtilsService, $location, $log){
    	
	var releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);
	
	$scope.releaseVersions = {
			online: [],
			offline: []
		};
	$scope.releasePackage = {releaseVersions:[]};
	
	$scope.utils = PackageUtilsService;
	
	$scope.$watch('releasePackage', function(newValue, oldValue) {
		$scope.releaseVersions = $scope.utils.updateVersionsLists(newValue);
	});
	
	var loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				$scope.releasePackage = result;
				})
					["catch"](function(message) {
						//FIXME how to handle errors + not present 
						$log.log('ReleasePackage not found');
						$location.path('/viewPackages');
					});
		} else {
			$location.path('/viewPackages');
		};
	};

	loadReleasePackage();

	$scope.goToViewPackages = function goToViewPackages() {
		$location.path('/viewPackages');
	};
}]);


