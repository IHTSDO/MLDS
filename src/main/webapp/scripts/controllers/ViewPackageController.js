'use strict';

angular.module('MLDS')
    .controller('ViewPackageController', 
    		['$scope', '$routeParams', 'PackagesService', 'PackageUtilsService', '$location', '$log', 'UserAffiliateService',
          function($scope, $routeParams, PackagesService, PackageUtilsService, $location, $log, UserAffiliateService){
    	
	var releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);
	
	$scope.releaseVersions = {
			online: [],
			offline: []
		};
	$scope.releasePackage = {releaseVersions:[]};
	
	$scope.isMembershipApproved = false;
	$scope.isMembershipIncomplete = false;
	$scope.isMembershipUnstarted = false;
	
	$scope.utils = PackageUtilsService;
	
	$scope.$watch('releasePackage', function(newValue, oldValue) {
		$scope.releaseVersions = $scope.utils.updateVersionsLists(newValue);
	});
	
	var setReleasePackage = function setReleasePackage(releasePackage) {
		$scope.releasePackage = releasePackage;
		$log.log(releasePackage);
		UserAffiliateService.promise.then(function() {
			$scope.isMembershipApproved = _.some(UserAffiliateService.approvedMemberships, function(member) { return member.key === releasePackage.member.key;});
			$scope.isMembershipIncomplete = _.some(UserAffiliateService.incompleteMemberships, function(member) {return member.key === releasePackage.member.key;});
			$scope.isMembershipUnstarted = !$scope.isMembershipApproved && !$scope.isMembershipIncomplete;
		});
	};

	var loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				setReleasePackage(result);
				})
					["catch"](function(message) {
						//FIXME how to handle errors + not present 
						$log.log('ReleasePackage not found');
						$location.path('/viewPackages');
					});
		} else {
			$location.path('/viewPackages');
		};
	};

	loadReleasePackage();

	$scope.goToViewPackages = function goToViewPackages() {
		$location.path('/viewPackages');
	};
	
}]);


