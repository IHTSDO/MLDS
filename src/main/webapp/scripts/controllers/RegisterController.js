'use strict';

angular.module('MLDS').controller('RegisterController', ['$rootScope', '$scope', '$translate', 'Register', '$location', '$log', 'CommercialUsageService','CountryService', 'Session', 'MemberService',
    function ($rootScope, $scope, $translate, Register, $location, $log, CommercialUsageService, CountryService, Session, MemberService) {
		$scope.availableCountries = CountryService.countries;
		
        $scope.success = null;
        $scope.error = {};
        $scope.registerAccount = {};
        $scope.confirmPassword = null;
        
        initFromMemberLanding();
        
        // bind the display name to our country object.
        $scope.$watch('countryCommonName', function(newValue){
        	var country = _.findWhere(CountryService.countries, {'commonName':newValue});
        	$scope.registerAccount.country = country;
        	//var excludedCountry = country && country.excludeRegistration;
        	//MLDS-785 there are no excluded countries
        	var excludedCountry = false;
        	$scope.createUserForm.country.$setValidity('excluded',!excludedCountry);
        });
        
        $scope.register = function () {
    		if ($scope.createUserForm.$invalid) {
    			$scope.createUserForm.attempted = true;
    			return;
    		}
            $scope.registerAccount.langKey = $translate.use();
            $scope.registerAccount.login = $scope.registerAccount.email;
            
            var initialPeriod = CommercialUsageService.generateRanges()[0];
            $scope.registerAccount.initialUsagePeriod = {
            		startDate: moment(initialPeriod.startDate).format('YYYY-MM-DD'),
            		endDate: moment(initialPeriod.endDate).format('YYYY-MM-DD')
            	};
            
            CommercialUsageService.generateRanges()[0];
            Register.save($scope.registerAccount,
                function (value, responseHeaders) {
                    $scope.error = {};
                    $scope.success = 'OK';
                    $location.path('/emailVerification');
                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.status === 304) {
                        $scope.error = {userExists: 'ERROR'};
                    } else if (httpResponse.status === 406) {
                    	$scope.error = {onBlocklist: 'ERROR'};
                    } else {
                    	$scope.error = {general: 'ERROR'};
                    }
                });
        };
        
        function initFromMemberLanding() {
        	if ($rootScope.memberLanding && $rootScope.memberLanding.key) {
        		CountryService.ready.then(function() {
        			var country = CountryService.countriesByIsoCode2[$rootScope.memberLanding.key];
        			$scope.countryCommonName = country.commonName;
        		});
        	}
        }
    }]);