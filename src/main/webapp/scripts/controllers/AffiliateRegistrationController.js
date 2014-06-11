'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', function ($scope, $log, UserRegistrationService, $location) {
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.submit = function affiliateRegistrationSubmit() {
				$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);

        		var httpPromise = UserRegistrationService.createApplication($scope.affiliateform);
        		
        		httpPromise.then(function() {
        			//UserSession.updateSession();
        			$location.path('/dashboard');
        		});
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);