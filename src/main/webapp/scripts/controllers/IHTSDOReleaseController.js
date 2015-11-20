'use strict';

angular.module('MLDS')
    .controller('IHTSDOReleaseController', 
    		['$scope', '$routeParams', 'PackagesService', 'PackageUtilsService', '$location', '$log', 'UserAffiliateService', 'ApplicationUtilsService', 'MemberService', 'StandingStateUtils', '$modal', 'ReleasePackageService', '$window',
          function($scope, $routeParams, PackagesService, PackageUtilsService, $location, $log, UserAffiliateService, ApplicationUtilsService, MemberService, StandingStateUtils, $modal, ReleasePackageService, $window) {
    	
	var releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);
	
	$scope.releaseVersions = {
			online: [],
			offline: []
		};
	$scope.releasePackage = $scope.releasePackage || {releaseVersions:[]};
	
	$scope.viewLicense = function (memberKey) {
		MemberService.getMemberLicense(memberKey);
	};
	
	$scope.utils = PackageUtilsService;
	
	$scope.$watch('releasePackage', function(newValue, oldValue) {
		$scope.releaseVersions = $scope.utils.updateVersionsLists(newValue);
	});
	
	var initReleasePackageState = function initReleasePackageState(releasePackage) {
	};

	var setReleasePackage = function setReleasePackage(releasePackage) {
		$scope.releasePackage = releasePackage;
		//$log.log('setReleasePackage', releasePackage);
		initReleasePackageState(releasePackage);
	};

	var loadReleasePackage = function loadReleasePackage() {
		if ($scope.releasePackage && $scope.releasePackage.releasePackageId) {
			// Already initialized as sub-controller
			initReleasePackageState($scope.releasePackage);
		} else if (releasePackageId) {
			// Main controller
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				setReleasePackage(result);
				})
			["catch"](function(message) {
				//FIXME how to handle errors + not present 
				$log.log('ReleasePackage not found');
				$scope.goToViewPackages();
			});
		} else {
			$scope.goToViewPackages();
		}
	};

	loadReleasePackage();

	$scope.goToViewPackages = function goToViewPackages() {
		$location.path('/ihtsdoReleases');
	};
		
	$scope.viewReleaseLicense = function() {
		ReleasePackageService.getReleaseLicense(releasePackageId);
	};
	
	$scope.downloadReleaseFile = function(downloadUrl) {
    	$window.open(downloadUrl, '_blank');
	};
	
}]);


