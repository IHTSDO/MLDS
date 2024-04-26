'use strict';

angular.module('MLDS').controller('EditReleaseFileModalController',
		['$scope', '$log', '$modalInstance', 'releasePackage', 'releaseVersion', 'releaseFile','ReleaseFilesService',
		 function($scope, $log,  $modalInstance, releasePackage, releaseVersion, releaseFile, ReleaseFilesService) {

    $scope.releasePackage = releasePackage;
    $scope.releaseVersion = releaseVersion;
	$scope.releaseFile = releaseFile;

	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];

	$scope.ok = function(form) {
		$scope.submitAttempted = true;
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);

		ReleaseFilesService.update(
				{
					releasePackageId : releasePackage.releasePackageId,
					releaseVersionId : releaseVersion.releaseVersionId
				}, $scope.releaseFile)
			.$promise.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure [36]: please try again later.'});
				$scope.submitting = false;
			});

	};

}]);
