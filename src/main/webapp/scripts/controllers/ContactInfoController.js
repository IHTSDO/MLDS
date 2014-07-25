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
        
        function loadAffiliate() {
        	AffiliateService.myAffiliate()
        		.then(function(result) {
        			var affiliate = result.data;
        			$log.log(affiliate);
        			if (affiliate) {
        				$scope.affiliate = affiliate;
        				$scope.type = /*'INDIVIDUAL'*/affiliate.type;
        				$scope.affiliateDetails = affiliate.affiliateDetails;
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