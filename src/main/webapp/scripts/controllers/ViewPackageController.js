'use strict';

angular.module('MLDS')
    .controller('ViewPackageController', ['$scope', '$routeParams', 'PackagesService', function($scope, $routeParams, PackagesService){
    	
	var releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);
	
	var loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				$scope.releasePackage = result;
				})
					["catch"](function(message) {
						//FIXME how to handle errors + not present
						$log.log('ReleasePackage not found');
						$location.path('/packageManagement');
					});
		} else {
			$location.path('/viewPackages');
		};
	}

	loadReleasePackage();

    }]);
