'use strict';

mldsApp.controller('AffiliateRegistrationReviewController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', '$modalInstance', 'CommercialUsageService', 'UserAffiliateService',
          function ($scope, $log, UserRegistrationService, $location, $modalInstance, CommercialUsageService, UserAffiliateService) {
        	CommercialUsageService.getUsageReport(CommercialUsageService.currentCommercialUsageReport.commercialUsageId);
        	$scope.CommercialUsageService = CommercialUsageService;
        	$scope.submitting = false;
        	$scope.alerts = [];
        	
        	
        	// FIXME MB this should be on the CommercialUsageService??
			$scope.commercialUsageInstitutionsByCountry = _.groupBy(CommercialUsageService.currentCommercialUsageReport.entries, 
    				function(entry){ return entry.country.isoCode2;});
			_.each($scope.commercialUsageInstitutionsByCountry, function(list, key) {
				$scope.commercialUsageInstitutionsByCountry[key] = _.sortBy(list, function(entry) {
					return entry.name.toLowerCase();
					});
			});
			
        	
        	
        	
			$scope.usageCountryCountslist = _.sortBy(CommercialUsageService.currentCommercialUsageReport.countries, function(count) {
				return count.country.commonName.toLowerCase();
			});
			
			$scope.usageCountryCountsPairs = [];
			var tempPair = [];
			_.each($scope.usageCountryCountslist, function(entry) {
				tempPair.push(entry);
				if (tempPair.length >= 2) {
					$scope.usageCountryCountsPairs.push(tempPair);
					tempPair = [];
				}
			});
			if (tempPair.length >= 1) {
				$scope.usageCountryCountsPairs.push(tempPair);					
			}
			
        	
    		$scope.ok = function() {
    			$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);
    			$scope.submitting = true;
    			$scope.alerts.splice(0, $scope.alerts.length);
    			
    			var httpPromise = UserRegistrationService.submitApplication($scope.affiliateform, $scope.applicationId);
    			
    			httpPromise.then(function() {
    				UserAffiliateService.refreshAffiliate();
    				$location.path('/dashboard');
    				$modalInstance.close();
    			})
    			["catch"](function(message) {
    				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
    				$scope.submitting = false;
    			});
			};
        }
    ]);