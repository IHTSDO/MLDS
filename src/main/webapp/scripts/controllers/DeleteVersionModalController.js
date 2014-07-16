'use strict';

angular.module('MLDS').controller('DeleteVersionModalController', ['$scope', '$log', '$modalInstance', 'releasePackage', 'releaseVersion', 'ReleaseVersionsService', 
    function ($scope, $log, $modalInstance, releasePackage, releaseVersion, ReleaseVersionsService) {
		$scope.releasePackage = releasePackage;
		$scope.releaseVersion = releaseVersion;
		$scope.alerts = [];
	
		$scope.ok = function() {
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			ReleaseVersionsService['delete']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
				.$promise.then(function(result) {
					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					$scope.submitting = false;
				});
		};
		
    }]);