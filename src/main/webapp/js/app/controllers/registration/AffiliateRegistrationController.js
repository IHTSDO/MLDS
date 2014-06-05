'use strict';

angular.module('MLDS')
    .controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', function ($scope, $log, UserRegistrationService, $location) {
        	
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.submit = function affiliateRegistrationSubmit() {
				$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);

        		var httpPromise = UserRegistrationService.createApplication($scope.affiliateform);
        		
        		httpPromise.then(function() {
        			$location.path('#/pendingRegistration');
        		});
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);