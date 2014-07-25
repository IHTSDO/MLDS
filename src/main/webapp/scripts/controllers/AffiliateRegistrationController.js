'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', '$modal', 'Session', 'Events',
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService, $modal, Session, Events) {
        	
        	var loadApplication = function() {
        		var queryPromise =  UserRegistrationService.getApplication();
        		
        		//FIXME entire page should fail if no existing application...
        		
        		queryPromise.success(function(data) {
    				$log.log("loadApplication", data);
    				var affiliateDetails = data.affiliateDetails ? data.affiliateDetails : {};
    				var address = affiliateDetails.address ? affiliateDetails.address : {};
    				var billingAddress = affiliateDetails.billingAddress ? affiliateDetails.billingAddress : {};
    				
    				$scope.approvalState = data.approvalState;
    				$scope.applicationId = data.applicationId;
    				
    				$scope.affiliateform.type = data.type ? data.type : '';
    				$scope.affiliateform.usageSubType = data.subType;
    				$scope.affiliateform.otherText = data.otherText ? data.otherText : '';
    				
    				$scope.affiliateform.contact.firstName = 
    				
    				$scope.affiliateform.contact.firstName = affiliateDetails.firstName ? affiliateDetails.firstName : Session.firstName;
    				$scope.affiliateform.contact.lastName = affiliateDetails.lastName ? affiliateDetails.lastName : Session.lastName;
    				$scope.affiliateform.contact.email = affiliateDetails.email ? affiliateDetails.email : Session.email;
    				$scope.affiliateform.contact.alternateEmail = affiliateDetails.alternateEmail ? affiliateDetails.alternateEmail : '';
    				$scope.affiliateform.contact.thirdEmail = affiliateDetails.thirdEmail ? affiliateDetails.thirdEmail : '';
    				$scope.affiliateform.contact.landlineNumber = affiliateDetails.landlineNumber ? affiliateDetails.landlineNumber : '';
    				$scope.affiliateform.contact.landlineExtension = affiliateDetails.landlineExtension ? affiliateDetails.landlineExtension : '';
    				$scope.affiliateform.contact.mobileNumber = affiliateDetails.mobileNumber ? affiliateDetails.mobileNumber : '';
    				$scope.affiliateform.organization.name = affiliateDetails.organizationName ? affiliateDetails.organizationName : '';
    				$scope.affiliateform.organization.type = affiliateDetails.organizationType ? affiliateDetails.organizationType : '';
    				$scope.affiliateform.organization.typeOther = affiliateDetails.organizationTypeOther ? affiliateDetails.organizationTypeOther : '';
    				
    				$scope.affiliateform.address.street = address.street ? address.street : '';
    				$scope.affiliateform.address.city = address.city ? address.city : '';
    				$scope.affiliateform.address.post = address.post ? address.post : '';
    				$scope.affiliateform.address.country = address.country ? address.country : '';
    				$scope.affiliateform.billing.street = billingAddress.street ? billingAddress.street : '';
    				$scope.affiliateform.billing.city = billingAddress.city ? billingAddress.city : '';
    				$scope.affiliateform.billing.postCode = billingAddress.post ? billingAddress.post : '';
    				$scope.affiliateform.billing.country = billingAddress.country ? billingAddress.country : '';
    				$scope.isSameAddress = checkAddresses(address, billingAddress);
    				
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
        	
            // bind the display name to our country object.
            $scope.$watch('affiliateform.address.country', function(newValue){
            	var country = _.findWhere(CountryService.countries, {'commonName':newValue});
            	$scope.selectedCountry = country;
            	var excludedCountry = country && country.excludeRegistration;
            	$scope.affiliateApplicationForm.country.$setValidity('excluded',!excludedCountry);
            	$scope.affiliateApplicationForm.countryIndividual.$setValidity('excluded',!excludedCountry);
            });
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.contact = {};
        	$scope.affiliateform.address = {};
        	$scope.affiliateform.billing = {};
        	$scope.affiliateform.organization = {};
        	
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
        		
        		$log.log('affiliateForm', affiliateForm)
        		
        		var modalInstance = $modal.open({
        			templateUrl: 'views/registration/affiliateRegistrationReview.html',
        			controller: 'AffiliateRegistrationReviewController',
        			size:'lg',
        			scope: $scope
        		});
        	};
        	
        	var checkAddresses = function(a, b) {
        		if( (a.street != '' && a.street === b.street) 
        				&& (a.city != '' && a.city === b.city)
        				&& (a.country != '' && a.country === b.country)
        				) {
        			return true;
        		}
        		return false;
        	};
        	
        	$scope.copyAddress = function() {
        		if($scope.isSameAddress) {
        			$scope.affiliateform.billing = {};
        			$scope.affiliateform.billing.street = $scope.affiliateform.address.street;
        			$scope.affiliateform.billing.city = $scope.affiliateform.address.city;
        			$scope.affiliateform.billing.post = $scope.affiliateform.address.post;
        			$scope.affiliateform.billing.country = $scope.affiliateform.address.country;
        		};
        	};
        	
        	$scope.affiliateTypeChanged = function() {
        		$scope.$broadcast(Events.affiliateTypeUpdated, $scope.affiliateform.type);
        		$scope.saveApplication();
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);