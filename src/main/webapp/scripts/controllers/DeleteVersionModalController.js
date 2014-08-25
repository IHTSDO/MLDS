'use strict';

angular.module('MLDS').controller('DeleteVersionModalController', ['$scope', '$log', '$modalInstance', 'releasePackage', 'releaseVersion', 'ReleaseVersionsService', 'ReleaseFilesService', 
    function ($scope, $log, $modalInstance, releasePackage, releaseVersion, ReleaseVersionsService, ReleaseFilesService) {
		$scope.releasePackage = releasePackage;
		$scope.releaseVersion = releaseVersion;
		$scope.alerts = [];
	
		$scope.ok = function() {
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			for(var i = 0; i < releaseVersion.releaseFiles.length; i++) {
				ReleaseFilesService['delete'](
						{	releasePackageId: $scope.packageEntity.releasePackageId,
							releaseVersionId: releaseVersion.releaseVersionId,
							releaseFileId: releaseVersion.releaseFiles[i].releaseFileId	});
			};
			
			ReleaseVersionsService['delete']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
				.$promise.then(function(result) {
					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
		};
		
    }]);