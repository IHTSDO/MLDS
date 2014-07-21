'use strict';

mldsApp.controller('AffiliateRegistrationReviewController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', '$modalInstance', 'CommercialUsageService',
          function ($scope, $log, UserRegistrationService, $location, UserSession, $modalInstance, CommercialUsageService) {
        	$scope.CommercialUsageService = CommercialUsageService;
        	$scope.submitting = false;
        	$scope.alerts = [];
        	
        	// FIXME MB this should be on the CommercialUsageService??
        	$scope.commercialUsageInstitutionsByCountry = 
        		_.groupBy(CommercialUsageService.currentCommercialUsageReport.entries, 
        				function(entry){ return entry.country.isoCode2;});
        	
    		$scope.ok = function() {
    			$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);
    			$scope.submitting = true;
    			$scope.alerts.splice(0, $scope.alerts.length);
    			
    			var httpPromise = UserRegistrationService.submitApplication($scope.affiliateform, $scope.applicationId);
    			
    			httpPromise.then(function() {
    				UserSession.updateSession();
    				$location.path('/dashboard');
    				$modalInstance.close();
    			})
    			["catch"](function(message) {
    				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
    				$scope.submitting = false;
    			});
			};
        }
    ]);