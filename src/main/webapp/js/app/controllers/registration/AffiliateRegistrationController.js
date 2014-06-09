'use strict';

angular.module('MLDS')
    .controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', function ($scope, $log, UserRegistrationService, $location, UserSession) {
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.$watch('affiliateform.type', function(newValue, oldValue) {
        		var firstTimeSet = newValue && !oldValue;
        		if (firstTimeSet) {
        			$scope.collapsePanel.type = true;
        		}
        	});
        	$scope.$watch('affiliateform.applicantType', function(newValue, oldValue) {
        		var firstTimeSet = newValue && !oldValue;
        		if (firstTimeSet) {
        			$scope.collapsePanel.applicantType = true;
        		}
        	});
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