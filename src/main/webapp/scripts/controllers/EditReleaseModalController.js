'use strict';

angular.module('MLDS').controller('EditReleaseModalController', ['$scope', '$log', '$modalInstance', '$location', 'PackagesService', 'releasePackage', 'MemberService', 
    function ($scope, $log, $modalInstance, $location, PackagesService, releasePackage, MemberService) {
		$scope.releasePackage = releasePackage;
		
		$scope.submitAttempted = false;
		$scope.submitting = false;
		$scope.alerts = [];
		$scope.members = MemberService.members;

		$scope.ok = function() {
			$scope.submitAttempted = true;
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			PackagesService.update($scope.releasePackage)
				.$promise.then(function(result) {
					$modalInstance.close(result);
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
		};

}]);