'use strict';

angular.module('MLDS').controller('RequestPasswordResetController', 
		['$scope', '$log', '$http', 
        function($scope, $log, $http) {
			$scope.passwordResetData = {};
			$scope.alerts = [];
			$scope.resetProcessStarted = false;
			
			$scope.requestResetEmail = function requestResetEmail() {
				$log.log('submit ', $scope.passwordResetData.email);
				$scope.alerts = [];
				
        		if ($scope.passwordResetForm.$invalid) {
        			$scope.passwordResetForm.attempted = true;
        			return;
        		}
        		
        		$http.post('/api/passwordReset', {email:$scope.passwordResetData.email})
        			.error(function(data){
        				$scope.alerts.push({ type: 'danger', msg: 'Email entered is not found in system.' });
        			})
        			.then(function(data){
        				$scope.alerts.push({ type: 'success', msg: 'Password reset process started, please check your email.' });
        				$scope.resetProcessStarted = true;
        			});
			};
			
		}]);
