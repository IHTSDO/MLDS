'use strict';

angular.module('MLDS').controller('DeleteInstitutionController', ['$scope', '$modalInstance', 'institutions', 'country', 'index', '$log', 
                                                       	function($scope, $modalInstance, institutions, country, index, $log) {
	$scope.country = country;
	$scope.institutions = institutions;
	$scope.index = index;
	
	$scope.cancel = function() {
		$modalInstance.dismiss();
	};
	
	$scope.removeInstitution = function() {
		$scope.institutions.splice(index, 1);
		$modalInstance.dismiss();
	};
}]);