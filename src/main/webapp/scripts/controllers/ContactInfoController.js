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
        
        function loadAffiliate() {
        	AffiliateService.myAffiliate()
        		.then(function(result) {
        			var affiliate = result.data;
        			$log.log(affiliate);
        			if (!affiliate || !affiliate.affiliateDetails) {
        				$log.log('No affiliates found...');
        				//FIXME should leave message on dashboard to explain why they got sent back to the dashboard
        				$location.path('/dashboard');
        				return;
        			}
    				$scope.affiliate = affiliate;
    				$scope.type = affiliate.type;
    				$scope.affiliateDetails = affiliate.affiliateDetails;
    				$scope.approved = AffiliateService.isApplicationApproved(affiliate);
        		})
    			["catch"](function(message) {
    				//FIXME handle affiliate loading error
    				$log.log('Error loading my affiliate');
    			});

        }
        
        loadAffiliate();
        
        // bind the display name to our country object.
        $scope.$watch('affiliateDetails.billingAddress.country', function(newValue){
        	var country = _.findWhere(CountryService.countries, {'commonName':newValue});
        	$scope.selectedCountry = country;
        });

        
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
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
				$scope.submitting = false;
			});
        };
        
        $scope.cancel = function() {
        	//FIXME $route.reload wasnt clearing out scope state - is there a better way?
        	window.location.reload();
        };
        
    }]);