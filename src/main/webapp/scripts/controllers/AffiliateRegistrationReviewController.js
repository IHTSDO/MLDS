'use strict';

mldsApp.controller('AffiliateRegistrationReviewController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'affiliateForm', '$modalInstance',
          function ($scope, $log, UserRegistrationService, $location, UserSession, affiliateForm, $modalInstance) {

        	$scope.affiliateform = affiliateForm;
        	
    		$scope.ok = function() {
    			$log.log('AffiliateRegistrationController submit()', $scope.affiliateform);
    			
    			var httpPromise = UserRegistrationService.createApplication($scope.affiliateform);
    			
    			httpPromise.then(function() {
    				UserSession.updateSession();
    				$location.path('/dashboard');
    				$modalInstance.close();
    			});
			};

			$scope.cancel = function() {
				$modalInstance.dismiss();
			};
        	
        }
    ]);