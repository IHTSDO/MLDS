'use strict';

angular.module('MLDS').controller('EditCurrentPackageModalController', ['$scope', '$log', '$modalInstance', 'releasePackage', 
    function ($scope, $log, $modalInstance, releasePackage) {
		$scope.releasePackage = releasePackage; 
    }]);