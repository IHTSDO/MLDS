'use strict';

angular.module('MLDS').controller('TakeOnlineModalController', ['$scope', '$log', '$modalInstance', 'releasePackage', 'releaseVersion', 'ReleaseVersionsService', 
    function ($scope, $log, $modalInstance, releasePackage, releaseVersion, ReleaseVersionsService) {
		$scope.releasePackage = releasePackage;
		$scope.releaseVersion = releaseVersion;
		$scope.notify = {notifyAffiliates: false};
		$scope.alerts = [];
	
		$scope.ok = function() {
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			$scope.releaseVersion.online = true;
			
			ReleaseVersionsService['update']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
				.$promise.then(function(updateResult) {
					if ($scope.notify.notifyAffiliates) {
						ReleaseVersionsService['notify']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
						.$promise.then(function(notifyResult) {
							$modalInstance.close(updateResult);
						})
						["catch"](function(message) {
							$log.warn('Failed to notify affiliates', message);
							$modalInstance.close(updateResult);
						});
					} else {
						$modalInstance.close(updateResult);
					}
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
		};
		
    }]);