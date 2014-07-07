'use strict';

mldsApp.controller('AffiliateRegistrationReviewController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', '$modalInstance', 'CommercialUsageService',
          function ($scope, $log, UserRegistrationService, $location, UserSession, $modalInstance, CommercialUsageService) {
        	$scope.CommercialUsageService = CommercialUsageService;
        	
        	// FIXME MB this should be on the CommercialUsageService??
        	$scope.commercialUsageInstitutionsByCountry = 
        		_.groupBy(CommercialUsageService.currentCommercialUsageReport.entries, 
        				function(entry){ return entry.country.isoCode2});
        	
    		$scope.ok = function() {
    			$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);
    			
    			var httpPromise = UserRegistrationService.submitApplication($scope.affiliateform);
    			
    			httpPromise.then(function() {
    				UserSession.updateSession();
    				$location.path('/dashboard');
    				$modalInstance.close();
    			});
			};
        }
    ]);