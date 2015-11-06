'use strict';

angular.module('MLDS').controller('RequestPasswordResetController', 
		['$scope', '$log', '$http', '$translate',
        function($scope, $log, $http, $translate) {
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
        				$scope.alerts.push({ type: 'danger', msg: $translate.instant('views.requestPasswordReset.emailNotFound') });
        			})
        			.then(function(data){
        				$scope.alerts.push({ type: 'success', msg: $translate.instant('views.requestPasswordReset.resetEmailSent') });
        				$scope.resetProcessStarted = true;
        			});
			};
			
		}]);
