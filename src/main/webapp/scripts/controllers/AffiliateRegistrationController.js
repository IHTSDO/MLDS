'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'UserSession', 'CountryService', '$modal', 'Session',
          function ($scope, $log, UserRegistrationService, $location, UserSession, CountryService, $modal, Session) {
        	
        	
        	var loadApplication = function() {
        		var queryPromise =  UserRegistrationService.getApplications();
        		
        		queryPromise.success(function(data) {
        			$log.log("loadApplication", data[0]);
        			$scope.affiliateform.type = data[0].type;
        			$scope.affiliateform.usageSubType = data[0].subType;
        			$scope.affiliateform.contact.name = data[0].name;
        			$scope.affiliateform.contact.email = data[0].email;
        			$scope.affiliateform.contact.alternateEmail = data[0].alternateEmail;
        			$scope.affiliateform.contact.thirdEmail = data[0].thirdEmail;
        			$scope.affiliateform.contact.phone = data[0].phoneNumber;
        			$scope.affiliateform.contact.extension = data[0].extension;
        			$scope.affiliateform.contact.mobilePhone = data[0].mobileNumber;
        			$scope.affiliateform.address.street = data[0].address;
        			$scope.affiliateform.address.city = data[0].city;
        			$scope.affiliateform.address.country = data[0].country;
        			$scope.affiliateform.billing.street = data[0].billingStreet;
        			$scope.affiliateform.billing.city = data[0].billingCity;
        			$scope.affiliateform.billing.country = data[0].billingCountry;
        			$scope.affiliateform.organization.name = data[0].organizationName;
        			$scope.affiliateform.organization.type = data[0].organizationType;
        			$scope.affiliateform.otherText = data[0].otherText;
        		});
        	};
        	
        	loadApplication();
        	
        	$scope.availableCountries = CountryService.countries;
        	
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.contact = {};
        	$scope.affiliateform.address = {};
        	$scope.affiliateform.billing = {};
        	$scope.affiliateform.organization = {};
        	
    		$scope.affiliateform.contact.name = Session.firstName;
        	$scope.affiliateform.contact.email = Session.email;
        	
        	$scope.saveApplication = function() {
    			UserRegistrationService.saveApplication($scope.affiliateform);
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
        	
        	$scope.copyAddress = function() {
        		if($scope.isSameAddress) {
        			$scope.affiliateform.billing = {};
        			$scope.affiliateform.billing.street = $scope.affiliateform.address.street;
        			$scope.affiliateform.billing.city = $scope.affiliateform.address.city;
        			$scope.affiliateform.billing.country = $scope.affiliateform.address.country;
        		};
        	};
        	
        	$scope.collapsePanel = {};
        	
        }
    ]);