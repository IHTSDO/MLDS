'use strict';

mldsApp.controller('RegisterController', ['$scope', '$translate', 'Register', '$location', '$log', 'CommercialUsageService',
    function ($scope, $translate, Register, $location, $log, CommercialUsageService) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.register = function () {
        	/*
            if ($scope.registerAccount.password != $scope.confirmPassword) {
                $scope.doNotMatch = "ERROR";
            } else {
            */
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
           // }
        }
    }]);