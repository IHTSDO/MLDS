'use strict';

angular.module('MLDS')
    .controller('ViewPackagesController', 
    		['$scope', '$log', 'PackagesService', 'PackageUtilsService', '$location', 'MemberService', 'UserAffiliateService',
           function ($scope, $log, PackagesService, PackageUtilsService, $location, MemberService, UserAffiliateService) {
			
	$scope.utils = PackageUtilsService;
	$scope.releasePackagesByMember = [];
	$scope.alerts = [];
	
	$scope.viewLicence = function (memberKey) {
		MemberService.getMemberLicence(memberKey);
	};
	
	loadReleasePackages();
		
	$scope.goToViewPackagePage = function goToViewPackagePage(releasePackageId) {
		$location.path('/viewPackages/viewPackage/'+ releasePackageId);
	};
	
	$scope.releasePackageOrderBy = UserAffiliateService.releasePackageOrderBy; 

	function loadReleasePackages() {
		$scope.alerts.splice(0, $scope.alerts.length);
		PackagesService.query().$promise
			.then(function(releasePackages) {
				$scope.releasePackagesByMember = _.chain(releasePackages)
					.filter(PackageUtilsService.isPackagePublished)
					.groupBy(function(value) {return value.member.key;})
					.map(function(packages, memberKey) {
						return {
							member: MemberService.membersByKey[memberKey], 
							packages: packages};})
					.value();
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
			});
	}
	
}]);
