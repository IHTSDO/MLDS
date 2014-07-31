'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', '$modal', 'Session', 'Events',
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService, $modal, Session, Events) {
        	
        	var loadApplication = function() {
        		var queryPromise =  UserRegistrationService.getApplication();
        		
        		//FIXME entire page should fail if no existing application...
        		
        		queryPromise.success(function(data) {
    				$log.log("loadApplication", data);
    				$scope.affiliateform = data;
    				$scope.approvalState = data.approvalState;
    				$scope.applicationId = data.applicationId;
    				$scope.isSameAddress = checkAddresses($scope.affiliateform.affiliateDetails.address, $scope.affiliateform.affiliateDetails.billingAddress);
    				
    				if (!UserRegistrationService.isApplicationWaitingForApplicant(data)) {
    					$log.log('Application does not require input from applicant');
    					$location.path('/dashboard');
    					return;
    				}
        		});
        	};
        	
        	loadApplication();
        	
        	$scope.approvalState = {};
        	$scope.availableCountries = CountryService.countries;
        	$scope.organizationTypes = UserRegistrationService.getOrganizationTypes();
        	$scope.affilliateControllerSharedBucket = {};
        	$scope.applicationId = null;
        	                        
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.affiliateDetails = {};
        	$scope.affiliateform.affiliateDetails.address = {};
        	$scope.affiliateform.affiliateDetails.billingAddress = {};
        	$scope.affiliateform.organization = {};
        	
            $scope.$watch('affiliateform.affiliateDetails.address.country', validateHomeCountry);

            function validateHomeCountry(newValue){
            	var country = newValue;
            	if (newValue && _.isString(newValue)) {
            		country = _.findWhere(CountryService.countries, {'commonName':newValue});
            	}
            	var excludedCountry = country && country.excludeRegistration;
            	$scope.affiliateApplicationForm.country.$setValidity('excluded',!excludedCountry);
            }

        	$scope.saveApplication = function() {
    			UserRegistrationService.saveApplication($scope.affiliateform, $scope.applicationId);
    			UserSession.reapplied();
        	};
        	
        	$scope.submit = $scope.saveApplication;
        	
        	$scope.openReviewModal = function(affiliateForm) {
        		// Only open review modal when form is valid
        		if ($scope.affiliateApplicationForm.$invalid) {
        			$scope.affiliateApplicationForm.attempted = true;
        			$scope.affilliateControllerSharedBucket.usageForm.attempted =true;
        			return;
        		}
        		
        		$log.log('affiliateForm', affiliateForm);
        		
        		var modalInstance = $modal.open({
        			templateUrl: 'views/registration/affiliateRegistrationReview.html',
        			controller: 'AffiliateRegistrationReviewController',
        			size:'lg',
        			scope: $scope
        		});
        	};
        	        	
        	var checkAddresses = function(a, b) {
        		
        		if( a && b && (a.street != '' && a.street === b.street) 
        				&& (a.city != '' && a.city === b.city)
        				&& (a.country != '' && a.country === b.country)
        				) {
        			return true;
        		}
        		return false;
        	};
        	
        	$scope.copyAddress = function() {
        		if($scope.isSameAddress) {
        			$scope.affiliateform.affiliateDetails.billingAddress = {};
        			$scope.affiliateform.affiliateDetails.billingAddress.street = $scope.affiliateform.affiliateDetails.address.street;
        			$scope.affiliateform.affiliateDetails.billingAddress.city = $scope.affiliateform.affiliateDetails.address.city;
        			$scope.affiliateform.affiliateDetails.billingAddress.post = $scope.affiliateform.affiliateDetails.address.post;
        			$scope.affiliateform.affiliateDetails.billingAddress.country = $scope.affiliateform.affiliateDetails.address.country;
        		};
        	};
        	
        	$scope.affiliateTypeChanged = function() {
        		$scope.$broadcast(Events.affiliateTypeUpdated, $scope.affiliateform.type);
        		$scope.saveApplication();
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);