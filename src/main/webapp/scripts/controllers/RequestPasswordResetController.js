'use strict';

angular.module('MLDS').controller('RequestPasswordResetController', 
		['$scope', '$log', '$http', 
        function($scope, $log, $http) {
			$scope.passwordResetData = {};
			
			$scope.requestResetEmail = function requestResetEmail() {
				$log.log('submit ', $scope.passwordResetData.email);
        		if ($scope.passwordResetForm.$invalid) {
        			$scope.passwordResetForm.attempted = true;
        			return;
        		}
        		
        		$http.post('/app/rest/requestPasswordReset', {email:$scope.passwordResetData.email});
			};
			
		}]);
