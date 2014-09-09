'use strict';

angular.module('MLDS').controller('ContactInfoController', ['$scope', '$log', '$timeout', '$route', '$location', 'Account', 'AffiliateService', 'CountryService',
    function ($scope, $log, $timeout, $route, $location, Account, AffiliateService, CountryService) {
        $scope.settingsAccount = Account.get();
        $scope.availableCountries = CountryService.countries;

    	$scope.submitting = false;
    	$scope.alerts = [];
    	$scope.form = {};
    	$scope.form.attempted = false;

        //FIXME review fields
        $scope.affiliate = null;
        $scope.affiliateDetails = null;
        $scope.type = null;
        $scope.approved = true;
        $scope.readOnly = false;

        function checkAddresses(a, b) {
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
    			$scope.affiliateDetails.billingAddress = {};
    			$scope.affiliateDetails.billingAddress.street = $scope.affiliateDetails.address.street;
    			$scope.affiliateDetails.billingAddress.city = $scope.affiliateDetails.address.city;
    			$scope.affiliateDetails.billingAddress.post = $scope.affiliateDetails.address.post;
    			$scope.affiliateDetails.billingAddress.country = $scope.affiliateDetails.address.country;
    		};
    	};
        
        function loadAffiliate() {
        	AffiliateService.myAffiliate()
        		.then(function(result) {
        			var affiliate = result.data;
        			if (affiliate && affiliate.affiliateDetails) {
        				$scope.affiliateDetails = affiliate.affiliateDetails;        				
        			} else if (affiliate && affiliate.application.affiliateDetails) {
        				$scope.affiliateDetails = affiliate.application.affiliateDetails;
        				$scope.readOnly = true;
        			} else {
        				$log.log('No affiliates found...');
        				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
        				return;
        			}
    				$scope.affiliate = affiliate;
    				$scope.type = affiliate.type;
    				$scope.approved = AffiliateService.isApplicationApproved(affiliate);
    				if (checkAddresses($scope.affiliateDetails, $scope.affiliateDetails.billingAddress)) {
    					$scope.isSameAddress = true;
    				}
        		})
    			["catch"](function(message) {
    				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
    				$log.log('Error loading my affiliate');
    			});

        }
        
        loadAffiliate();
                
        $scope.save = function () {
    		if ($scope.form.$invalid) {
    			$scope.form.attempted = true;
    			return;
    		}

    		$scope.submitting = true;
    		$scope.alerts.splice(0, $scope.alerts.length);

    		AffiliateService.updateAffiliateDetails($scope.affiliate.affiliateId, $scope.affiliateDetails)
    			.then(function(result) {
    				$scope.affiliateDetails = result.data;
    				$scope.submitting = false;
    				$scope.alerts.push({type: 'success', msg: 'Contact information has been successfully saved.'});
    			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
				$scope.submitting = false;
			});
        };
        
        $scope.cancel = function() {
        	//FIXME $route.reload wasnt clearing out scope state - is there a better way?
        	window.location.reload();
        };
    }]);