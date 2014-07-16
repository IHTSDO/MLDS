'use strict';

angular.module('MLDS')
    .controller('ViewPackagesController', ['$scope', '$log', 'PackagesService', function ($scope, $log, PackagesService) {
			
    		$scope.releasePackages = PackagesService.query();

    }]);
