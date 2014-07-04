'use strict';

mldsApp.controller('RegisterController', ['$scope', '$translate', 'Register', '$location', '$log',
    function ($scope, $translate, Register, $location, $log) {
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
                $scope.registerAccount.login =$scope.registerAccount.email; 
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