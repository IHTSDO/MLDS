'use strict';

angular.module('MLDS')
    .controller('ViewReleasesController', 
    		['$scope', '$log', 'PackageUtilsService', '$location', 'MemberService', 'UserAffiliateService','releasePackagesQueryResult',
           function ($scope, $log, PackageUtilsService, $location, MemberService, UserAffiliateService, releasePackagesQueryResult) {
			
	$scope.utils = PackageUtilsService;
	$scope.releasePackagesByMember = [];
	$scope.alerts = [];
	
	$scope.isMembershipApproved = UserAffiliateService.isMembershipApproved;
	
	$scope.viewLicence = function (memberKey) {
		MemberService.getMemberLicence(memberKey);
	};
	
	$scope.goToViewPackagePage = function goToViewPackagePage(releasePackageId) {
		$location.path('/viewReleases/viewRelease/'+ releasePackageId);
	};
	
	$scope.releasePackageOrderBy = UserAffiliateService.releasePackageOrderBy; 

	var releasePackages = releasePackagesQueryResult;
				$scope.releasePackagesByMember = _.chain(releasePackages)
					.filter(PackageUtilsService.isPackagePublished)
					.groupBy(function(value) {return value.member.key;})
					.map(function(packages, memberKey) {
						return {
							member: MemberService.membersByKey[memberKey], 
							packages: packages};})
					.value();
}]);
