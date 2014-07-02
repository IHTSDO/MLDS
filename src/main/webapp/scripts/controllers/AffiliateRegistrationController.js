'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', '$modal', 'Session',
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService, $modal, Session) {
        	
        	$scope.availableCountries = CountryService.countries;
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
    		$scope.affiliateform.name = Session.firstName + (Session.lastName ? ' '+ Session.lastName : '');
    		
        	$scope.affiliateform.email = Session.email;
        	
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
        	
        	$scope.copyAddress = function() {
        		if($scope.isSameAddress) {
        			$scope.affiliateform.billingAddress = $scope.affiliateform.address;
        			$scope.affiliateform.billingCity = $scope.affiliateform.city;
        			$scope.affiliateform.billingCountry = $scope.affiliateform.country;
        		};
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);