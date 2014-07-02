'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', 
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService) {
        	
        	$scope.availableCountries = CountryService.countries;
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.submit = function affiliateRegistrationSubmit() {
        		if ($scope.affiliateApplicationForm.$invalid) {
        			$scope.affiliateApplicationForm.attempted = true;
        			return;
        		}
        		
				$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);

        		var httpPromise = UserRegistrationService.createApplication($scope.affiliateform);
        		
        		httpPromise.then(function() {
        			UserSession.updateSession();
        			$location.path('/dashboard');
        		});
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);