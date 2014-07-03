'use strict';

angular.module('MLDS').controller('ResetPasswordController', 
		['$scope', '$log', '$http', 
        function($scope, $log, $http) {
			$scope.resetPassword = {};
			
	        $scope.success = null;
	        $scope.error = null;
	        $scope.doNotMatch = null;
	        $scope.submit = function () {
        		if ($scope.form.$invalid) {
        			// FIXME MB can we put this into bs-validation?
        			$scope.form.attempted = true;
        			return;
        		}
	            if ($scope.resetPassword.password != $scope.resetPassword.confirmPassword) {
	                $scope.doNotMatch = "ERROR";
	            } else {
	                $scope.doNotMatch = null;
	                $log.log('success', $scope.resetPassword.password);
	                /*
	                Password.save($scope.password,
	                    function (value, responseHeaders) {
	                        $scope.error = null;
	                        $scope.success = 'OK';
	                    },
	                    function (httpResponse) {
	                        $scope.success = null;
	                        $scope.error = "ERROR";
	                    });*/
	            }
	        };
		}]);
