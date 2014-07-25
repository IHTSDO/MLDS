'use strict';

angular.module('MLDS').controller('ContactInfoController', ['$scope', '$log', '$timeout', '$route', 'Account', 'AffiliateService', 'CountryService',
    function ($scope, $log, $timeout, $route, Account, AffiliateService, CountryService) {
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

        function insertFakeDetails(affiliate) {
        	affiliate.affiliateDetails = {
        			firstName: 'John',
        			lastName: 'Smith',
        			email: 'email@com',
        			alternateEmail: 'alternative@com',
        			thirdEmail: 'third@com',
        			address: {
	        			street: 'street',
	        			city: 'city',
	        			post: 'post',
	        			country: 'Canada'
        			},
        			organizationName: 'Organization Name',
        			billingAddress: {
	        			street: 'b street',
	        			city: 'b city',
	        			post: 'b post',
	        			country: 'Botswana'
        			},
        			landlineNumber: '+1 4156 762 0032',
        			landlineExtension: '123',
        			mobileNumber: '+1 416 999 99999'
        			
        	};
        }
        
        function loadAffiliate() {
        	//FIXME use single affiliate service call...
        	AffiliateService.myAffiliates()
        		.then(function(result) {
        			var affiliates = result.data;
        			if (affiliates && affiliates.length > 0) {
        				$scope.affiliate = affiliates[0];
        				$scope.type = /*'INDIVIDUAL'*/$scope.affiliate.type;
        				insertFakeDetails($scope.affiliate);
        				$scope.affiliateDetails = $scope.affiliate.affiliateDetails;
        			} else {
        				$log.log('No affiliates found...');
        			}
        		})
    			["catch"](function(message) {
    				//FIXME handle affiliate loading error
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

    		//FIXME implemented
    		$timeout(function() {
    			$scope.alerts.push({type: 'danger', msg: 'FIXME NOT YET IMPLEMENTED, please try again later.'});
    			$scope.submitting = false;
    			
    		}, 300);

			/*
            Account.save($scope.settingsAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                    $scope.settingsAccount = Account.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
                */
        };
        
        $scope.cancel = function() {
        	//FIXME $route.reload wasnt clearing out scope state - is there a better way?
        	window.location.reload();
        };
    }]);