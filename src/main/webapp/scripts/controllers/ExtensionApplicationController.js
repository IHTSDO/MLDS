'use strict';

angular.module('MLDS').controller('ExtensionApplicationController', ['$scope', '$log',
                                                       	function($scope, $log) {
	
	$log.info('ExtensionApplicationController');
	
	$scope.extensionForm = {};
	
	$scope.extensionForm.submit = function(){
		$log.log('extensionForm.submit', $scope.extensionForm);
		$scope.extensionForm.attempted = true;
	};
		

}]);