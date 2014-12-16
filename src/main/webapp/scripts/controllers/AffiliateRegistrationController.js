'use strict';

mldsApp.controller('AffiliateRegistrationController',
        [ '$scope', '$log', 'UserRegistrationService', '$location', 'CountryService', '$modal', 'Session', 'Events', 'ApplicationUtilsService', 'MemberService',
          function ($scope, $log, UserRegistrationService, $location, CountryService, $modal, Session, Events, ApplicationUtilsService, MemberService) {
        	
        	var loadApplication = function() {
        		var queryPromise =  UserRegistrationService.getApplication();
        		
        		//FIXME entire page should fail if no existing application...
        		
        		queryPromise.success(function(data) {
    				$log.log("loadApplication", data);
    				$scope.affiliateform = data;
    				$scope.approvalState = data.approvalState;
    				$scope.applicationId = data.applicationId;
    				$scope.isSameAddress = checkAddresses($scope.affiliateform.affiliateDetails.address, $scope.affiliateform.affiliateDetails.billingAddress);
    				
    				if (!ApplicationUtilsService.isApplicationWaitingForApplicant(data)) {
    					$log.log('Application does not require input from applicant');
    					$location.path('/dashboard');
    					return;
    				}
        		});
        	};
        	
        	loadApplication();
        	
        	$scope.viewLicense = function (memberKey) {
    			MemberService.getMemberLicense(memberKey);
    		};
        	
        	$scope.approvalState = {};
        	$scope.availableCountries = CountryService.countries;
        	$scope.organizationTypes = ApplicationUtilsService.getOrganizationTypes();
        	$scope.affilliateControllerSharedBucket = {};
        	$scope.applicationId = null;
        	                        
        	window.regScope = $scope;
        	$scope.affiliateform = {};
        	
        	$scope.affiliateform.affiliateDetails = {};
        	$scope.affiliateform.affiliateDetails.address = {};
        	$scope.affiliateform.affiliateDetails.billingAddress = {};
        	$scope.affiliateform.organization = {};
        	
            $scope.saveApplication = function() {
    			UserRegistrationService.saveApplication($scope.affiliateform, $scope.applicationId);
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
        			$scope.affiliateform.affiliateDetails.billingAddress = angular.copy($scope.affiliateform.affiliateDetails.address);
        		};
        	};
        	
        	$scope.$watch('affiliateform.type',function(newValue,oldValue) {
        		if (oldValue) {
        			$scope.affiliateform.subType = '';
            		$scope.affiliateform.otherText = '';
            		$scope.saveApplication();
        		}
        		$scope.$broadcast(Events.affiliateTypeUpdated, $scope.affiliateform.type);
        	});
        	
        	$scope.collapsePanel = {};
        	$scope.agreementTypeOptions = ['AFFILIATE_NORMAL', 'AFFILIATE_RESEARCH', 'AFFILIATE_PUBLIC_GOOD'];
        }
    ]);