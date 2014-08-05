'use strict';

mldsApp.controller('RegisterController', ['$scope', '$translate', 'Register', '$location', '$log', 'CommercialUsageService','CountryService',
    function ($scope, $translate, Register, $location, $log, CommercialUsageService,CountryService) {
		$scope.availableCountries = CountryService.countries;
		
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.registerAccount = {};
        $scope.confirmPassword = null;
        
        // bind the display name to our country object.
        $scope.$watch('countryCommonName', function(newValue){
        	var country = _.findWhere(CountryService.countries, {'commonName':newValue});
        	$scope.registerAccount.country = country;
        	var excludedCountry = country && country.excludeRegistration;
        	$scope.createUserForm.country.$setValidity('excluded',!excludedCountry);
        });
        
        $scope.register = function () {
        	$log.log('register', $scope.registerAccount);
    		if ($scope.createUserForm.$invalid) {
    			$scope.createUserForm.attempted = true;
    			return;
    		}
            $scope.registerAccount.langKey = $translate.use();
            $scope.doNotMatch = null;
            $scope.registerAccount.login = $scope.registerAccount.email;
            $log.log('initialUsagePeriod', CommercialUsageService.generateRanges());
            
            var initialPeriod = CommercialUsageService.generateRanges()[0];
            $scope.registerAccount.initialUsagePeriod = {
            		startDate: moment(initialPeriod.startDate).format('YYYY-MM-DD'),
            		endDate: moment(initialPeriod.endDate).format('YYYY-MM-DD')
            	};
            
            CommercialUsageService.generateRanges()[0];
            Register.save($scope.registerAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.errorUserExists = null;
                    $scope.success = 'OK';
                    $location.path('/emailVerification');
                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.status === 304) {
                        $scope.error = null;
                        $scope.errorUserExists = "ERROR";
                    } else {
                        $scope.error = "ERROR";
                        $scope.errorUserExists = null;
                    }
                });
        };
    }]);