'use strict';

angular.module('MLDS').controller('AddEditReleaseVersionModalController', 
		['$scope', '$log', '$modalInstance', 'PackagesService', 'releasePackage', 'ReleaseVersionsService', 'releaseVersion',
		 function($scope, $log,  $modalInstance, PackagesService, releasePackage, ReleaseVersionsService, releaseVersion) {
	
	var isNewObject = !(releaseVersion.releaseVersionId);
			
	$scope.isNewObject = isNewObject;
	$scope.releasePackage = releasePackage;
	$scope.releaseVersion = releaseVersion;
	
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.ok = function(form) {
		$scope.submitAttempted = true;
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		ReleaseVersionsService[isNewObject?'save':'update']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
			.$promise.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};

}]);