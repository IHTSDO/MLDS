'use strict';

angular.module('MLDS').controller('TakeOfflineModalController', ['$scope', '$log', '$modalInstance', 'releasePackage', 'releaseVersion', 'ReleaseVersionsService', 
    function ($scope, $log, $modalInstance, releasePackage, releaseVersion, ReleaseVersionsService) {
		$scope.releasePackage = releasePackage;
		$scope.releaseVersion = releaseVersion;
		$scope.alerts = [];
	
		$log.log('TakeOfflineModalController');
		
		$scope.ok = function() {
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			$scope.releaseVersion.online = true;
			
			$log.log($scope.releaseVersion);
			
			ReleaseVersionsService['update']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
				.$promise.then(function(result) {
					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					$scope.submitting = false;
				});
		};
		
    }]);