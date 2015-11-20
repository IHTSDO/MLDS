'use strict';

angular.module('MLDS').controller('DeleteAffiliateController', ['$scope', '$modalInstance', 'affiliate', '$log', 'AffiliateService', 
                                                       	function($scope, $modalInstance, affiliate, $log, AffiliateService) {
	$scope.affiliate = affiliate;
	
	$scope.attemptedSubmit = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.deleteAffiliate = function() {
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		AffiliateService.deleteAffiliate($scope.affiliate)
			.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
	};
}]);