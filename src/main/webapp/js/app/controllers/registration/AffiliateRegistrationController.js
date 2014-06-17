'use strict';

angular.module('MLDS')
    .controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', function ($scope, $log, UserRegistrationService, $location, UserSession) {
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.submit = function affiliateRegistrationSubmit() {
        		if ($scope.affiliateApplicationForm.$invalid) {
        			$scope.affiliateform.wasAttempted = true;
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