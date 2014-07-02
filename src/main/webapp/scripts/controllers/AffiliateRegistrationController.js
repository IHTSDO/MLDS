'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', '$modal',
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService, $modal) {
        	
        	$scope.availableCountries = CountryService.countries;
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	
        	$scope.openReviewModal = function(affiliateForm) {
        		// Only open review modal when form is valid
        		if ($scope.affiliateApplicationForm.$invalid) {
        			$scope.affiliateApplicationForm.attempted = true;
        			return;
        		}
        		
        		$log.log('affiliateForm', affiliateForm)
        		
        		var modalInstance = $modal.open({
        			templateUrl: 'views/registration/affiliateRegistrationReview.html',
        			controller: 'AffiliateRegistrationReviewController',
        			size:'lg',
        			resolve: {
        				affiliateForm: function() {
        					return affiliateForm;
        				}
        			}
        		});
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);