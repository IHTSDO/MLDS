'use strict';

angular.module('MLDS')
    .controller('IHTSDOReleasesController',
    		['$scope', '$log', 'Session', 'PackageUtilsService', '$location', 'MemberService', 'UserAffiliateService','releasePackagesQueryResult', 'MemberPackageService',
           function ($scope, $log, Session, PackageUtilsService, $location, MemberService, UserAffiliateService, releasePackagesQueryResult, MemberPackageService) {

	$scope.utils = PackageUtilsService;
	$scope.member = MemberService.ihtsdoMember;
	$scope.onlinePackages = [];
	$scope.offlinePackages = [];
	$scope.alphabetaPackages = [];
	$scope.alerts = [];

	$scope.viewLicense = function () {
		MemberService.getMemberLicense(MemberService.ihtsdoMemberKey);
	};

	$scope.goToPackage = function goToPackage(releasePackage) {
		$location.path('/ihtsdoReleases/ihtsdoRelease/'+encodeURIComponent(releasePackage.releasePackageId));
	};

    $scope.onlinePackages = PackageUtilsService.releasePackageSort(
    	_.chain(releasePackagesQueryResult)
	        .filter(function (p) {return MemberService.isIhtsdoMember(p.member);})
	        .filter(PackageUtilsService.isPackagePublished)
	        .value());
    $scope.offlinePackages =
    	_.chain(releasePackagesQueryResult)
	        .filter(function (p) {return MemberService.isIhtsdoMember(p.member);})
	        .reject(PackageUtilsService.isPackagePublished)
	        .reject(PackageUtilsService.isPackageNotPublished)
	        .reject(PackageUtilsService.isPackageFullyArchived)
			.sortBy('createdAt')
	        .value();
    $scope.alphabetaPackages =
    	_.chain(releasePackagesQueryResult)
	        .filter(function (p) {return MemberService.isIhtsdoMember(p.member);})
	        .reject(PackageUtilsService.isPackagePublished)
	        .reject(PackageUtilsService.isPackageOffline)
	        .reject(PackageUtilsService.isPackageEmpty)
			.sortBy('createdAt')
	        .value();


}]);
