'use strict';

angular.module('MLDS').controller('AddReleaseFileModalController', 
		['$scope', '$log', '$modalInstance', 'releasePackage', 'releaseVersion', 'ReleaseFilesService', 
		 function($scope, $log,  $modalInstance, releasePackage, releaseVersion, ReleaseFilesService) {
	
	var isNewObject = !(releaseVersion.releaseVersionId);
			
	$scope.isNewObject = isNewObject;
	$scope.releasePackage = releasePackage;
	$scope.releaseVersion = releaseVersion;
	$scope.releaseFile = {};
	
	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.ok = function(form) {
		$scope.submitAttempted = true;
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		ReleaseFilesService.save(
				{
					releasePackageId : releasePackage.releasePackageId,
					releaseVersionId : releaseVersion.releaseVersionId
				}, $scope.releaseFile)
			.$promise.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};

}]);