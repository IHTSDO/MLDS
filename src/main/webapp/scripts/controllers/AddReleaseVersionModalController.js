'use strict';

angular.module('MLDS').controller('AddReleaseVersionModalController', 
		['$scope', '$log', '$modalInstance', 'PackagesService', 'releasePackage', '$resource',
		 function($scope, $log,  $modalInstance, PackagesService, releasePackage, $resource) {
	
			
	$scope.releasePackage = releasePackage;
	$scope.releaseVersion = {}; //releasePackageId : releasePackage.releasePackageId};
	
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.ok = function(form) {
		$scope.submitAttempted = true;
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		
		var versions = $resource('app/rest/releasePackages/:releasePackageId/releaseVersions', {releasePackageId: '@releasePackageId', releaseVersionId: '@releaseVersionId'}, {
			update: {method: 'PUT'}
		});
		
		// FIXME create new version
		versions.save({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
			.$promise.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
				$scope.submitting = false;
			});
	};

}]);