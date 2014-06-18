'use strict';

angular.module('MLDS').controller('AddInstitutionController', ['$scope', '$modalInstance', 'country', 
                                                       	function($scope, $modalInstance, country) {
	$scope.country = country;
	
	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};
}]);