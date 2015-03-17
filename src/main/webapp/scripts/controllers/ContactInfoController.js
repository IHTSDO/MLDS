'use strict';

angular.module('MLDS').controller('ContactInfoController', ['$scope', '$http', '$log', '$timeout', '$route', '$location', 'Account', 'AffiliateService', 'CountryService',
    function ($scope, $http, $log, $timeout, $route, $location, Account, AffiliateService, CountryService) {
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

        $scope.billingHide = false;
        var loadJson = $http.get('/i18n/en.json');
        $scope.copyAddressMember = function(global, member) {
            if ($.inArray(member,CountryService.countriesUsingMLDS) != -1) {
                $scope.billingHide = true;
                $scope.addressOverride = true;
                $scope.affiliateDetails.billingAddress.street = $scope.affiliateDetails.address.street;
    			$scope.affiliateDetails.billingAddress.city = $scope.affiliateDetails.address.city;
    			$scope.affiliateDetails.billingAddress.post = $scope.affiliateDetails.address.post;
    			$scope.affiliateDetails.billingAddress.country = $scope.affiliateDetails.address.country;
            };
        };
        $scope.loadJson = function(){
            loadJson.success(function(data) {
                $scope.copyAddressMember(data.global, $scope.affiliateDetails.address.country.isoCode2);
            });
        }

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
    		if($scope.isSameAddress || $scope.addressOverride) {
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

    				if (!$scope.approved) {
    					$scope.readOnly = true;
    				}
                    $scope.loadJson();
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
       		$location.path('/dashboard');
        };
    }]);
