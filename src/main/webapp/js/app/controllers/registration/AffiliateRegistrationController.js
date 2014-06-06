'use strict';

angular.module('MLDS')
    .controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', function ($scope, $log, UserRegistrationService, $location, UserSession) {
        	
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.submit = function affiliateRegistrationSubmit() {
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