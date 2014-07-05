'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', '$modal', 'Session',
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService, $modal, Session) {
        	
        	var loadApplication = function() {
        		var queryPromise =  UserRegistrationService.getApplication();
        		
        		queryPromise.success(function(data) {
    				$log.log("loadApplication", data);
    				$scope.affiliateform.type = data.type ? data.type : '';
    				$scope.affiliateform.usageSubType = data.subType;
    				$scope.affiliateform.contact.name = data.name ? data.name : Session.firstName;
    				$scope.affiliateform.contact.email = data.email ? data.email : Session.email;
    				$scope.affiliateform.contact.alternateEmail = data.alternateEmail ? data.alternateEmail : '';
    				$scope.affiliateform.contact.thirdEmail = data.thirdEmail ? data.thirdEmail : '';
    				$scope.affiliateform.contact.phone = data.phoneNumber ? data.phoneNumber : '';
    				$scope.affiliateform.contact.extension = data.extension ? data.extension : '';
    				$scope.affiliateform.contact.mobilePhone = data.mobileNumber ? data.mobileNumber : '';
    				$scope.affiliateform.address.street = data.address ? data.address : '';
    				$scope.affiliateform.address.city = data.city ? data.city : '';
    				$scope.affiliateform.address.postCode = data.postCode ? data.postCode : '';
    				$scope.affiliateform.address.country = data.country ? data.country : '';
    				$scope.affiliateform.billing.street = data.billingStreet ? data.billingStreet : '';
    				$scope.affiliateform.billing.city = data.billingCity ? data.billingCity : '';
    				$scope.affiliateform.billing.postCode = data.billingPostCode ? data.billingPostCode : '';
    				$scope.affiliateform.billing.country = data.billingCountry ? data.billingCountry : '';
    				$scope.isSameAddress = checkAddresses($scope.affiliateform.address, $scope.affiliateform.billing);
    				$scope.affiliateform.organization.name = data.organizationName ? data.organizationName : '';
    				$scope.affiliateform.organization.type = data.organizationType ? data.organizationType : '';
    				$scope.affiliateform.organization.typeOther = data.organizationTypeOther ? data.organizationTypeOther : '';
    				$scope.affiliateform.otherText = data.otherText ? data.otherText : '';
        		});
        	};
        	
        	loadApplication();
        	
        	$scope.availableCountries = CountryService.countries;
        	$scope.organizationTypes = UserRegistrationService.getOrganizationTypes();
        	
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.contact = {};
        	$scope.affiliateform.address = {};
        	$scope.affiliateform.billing = {};
        	$scope.affiliateform.organization = {};
        	
        	$scope.saveApplication = function() {
    			UserRegistrationService.saveApplication($scope.affiliateform);
    			UserSession.reapplied();
        	};
        	
        	$scope.submit = $scope.saveApplication;
        	
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
        	
        	var checkAddresses = function(a, b) {
        		if( (a.street === b.street) 
        				&& (a.city === b.city)
        				&& (a.country === b.country)
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
        			$scope.affiliateform.billing.postCode = $scope.affiliateform.address.postCode;
        			$scope.affiliateform.billing.country = $scope.affiliateform.address.country;
        		};
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);