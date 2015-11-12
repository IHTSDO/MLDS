'use strict';

angular.module('MLDS')
    .controller('IHTSDOReleasesController', 
    		['$scope', '$log', 'Session', 'PackageUtilsService', '$location', 'MemberService', 'UserAffiliateService','releasePackagesQueryResult', 'MemberPackageService',
           function ($scope, $log, Session, PackageUtilsService, $location, MemberService, UserAffiliateService, releasePackagesQueryResult, MemberPackageService) {
    			
	$scope.utils = PackageUtilsService;
	$scope.releasePackages = [];
	$scope.alerts = [];
	
	$scope.viewLicense = function () {
		MemberService.getMemberLicense(MemberService.ihtsdoMemberKey);
	};
	
	$scope.goToIHTSDOPackagePage = function goToViewPackagePage(releasePackageId) {
		$location.path('/ihtsdoReleases/ihtsdoRelease/'+ releasePackageId);
	};
	
    $scope.releasePackages = PackageUtilsService.releasePackageSort( 
    	_.chain(releasePackagesQueryResult)
        .filter(PackageUtilsService.isPackagePublished)
        .filter(function (p) {return MemberService.isIhtsdoMember(p.member);})
        .value());
}]);
