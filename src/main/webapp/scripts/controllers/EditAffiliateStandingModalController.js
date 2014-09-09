'use strict';

angular.module('MLDS').controller('EditAffiliateStandingModalController', ['$scope', '$log', '$modalInstance', '$location', 'AffiliateService', 'affiliate', 
    function ($scope, $log, $modalInstance, $location, AffiliateService, affiliate) {
		$scope.affiliate = affiliate;
		
		$scope.submitAttempted = false;
		$scope.submitting = false;
		$scope.alerts = [];
		
		$scope.ok = function() {
			$scope.submitAttempted = true;
			$scope.submitting = true;
			$scope.alerts.splice(0, $scope.alerts.length);
			
			AffiliateService.updateAffiliate($scope.affiliate)
				.then(function(result) {
					$modalInstance.close(result.data);
					$scope.submitting = false;
				})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
		};

		$scope.affiliateActiveDetails = function affiliateActiveDetails() {
			return affiliate.affiliateDetails || affiliate.application.affiliateDetails;
		};
}]);