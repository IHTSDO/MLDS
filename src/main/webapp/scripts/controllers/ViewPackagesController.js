'use strict';

angular.module('MLDS')
    .controller('ViewPackagesController', 
    		['$scope', '$log', 'PackagesService', 'PackageUtilsService', '$location',
           function ($scope, $log, PackagesService, PackageUtilsService, $location) {
			
	$scope.utils = PackageUtilsService;
	$scope.releasePackagesByMember = [];
	$scope.alerts = [];
	
	loadReleasePackages();
		
	$scope.goToViewPackagePage = function goToViewPackagePage(releasePackageId) {
		$location.path('/viewPackages/viewPackage/'+ releasePackageId);
	};

	function loadReleasePackages() {
		$scope.alerts.splice(0, $scope.alerts.length);
		PackagesService.query().$promise
			.then(function(releasePackages) {
				$scope.releasePackagesByMember = _.chain(releasePackages)
					.filter(PackageUtilsService.isPackagePublished)
					.groupBy(function(value) {return value.member.key;})
					.pairs()
					.value();
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
			});
	}
}]);
