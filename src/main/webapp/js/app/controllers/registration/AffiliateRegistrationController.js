'use strict';

angular.module('MLDS')
    .controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', function ($scope, $log, UserRegistrationService) {
        	
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.submit = function affiliateRegistrationSubmit() {
				$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);

        		UserRegistrationService.createApplication($scope.affiliateform);
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);