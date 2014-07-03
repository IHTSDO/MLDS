'use strict';

angular.module('MLDS').controller('ResetPasswordController', 
		['$scope', '$log', '$resource', '$routeParams', '$timeout', '$location', 
        function($scope, $log, $resource, $routeParams, $timeout, $location) {
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
	                $resource('/app/rest/passwordReset/:id', {id:$routeParams.token})
	                	.save({ password : $scope.resetPassword.password })
	        			.$promise.then(function(data){
	                        $scope.error = null;
	                        $scope.success = 'OK';
	                        
	                        $timeout(function() { $location.path('/login'); }, 2000);
	        			},function(response){
	                        $scope.success = null;
	                        $scope.error={};
	                        if (response.status == 404) {
	                        	$scope.error.expired = true;
	                        } else {
	                        	$scope.error.server = true;
	                        }
	        			});

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
