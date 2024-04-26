'use strict';

angular.module('MLDS')
    .controller('ViewReleasesController',
    		['$scope', '$log', 'Session', 'PackageUtilsService', '$location', 'MemberService', 'UserAffiliateService','releasePackagesQueryResult', 'MemberPackageService',
           function ($scope, $log, Session, PackageUtilsService, $location, MemberService, UserAffiliateService, releasePackagesQueryResult, MemberPackageService) {
	$scope.utils = PackageUtilsService;
	$scope.releasePackagesByMember = [];
	$scope.alerts = [];

	$scope.alphaReleasePackagesByMember = [];

	$scope.isMembershipApproved = UserAffiliateService.isMembershipApproved;

	$scope.viewLicense = function (memberKey) {
		MemberService.getMemberLicense(memberKey);
	};

	$scope.goToViewPackagePage = function goToViewPackagePage(releasePackageId) {
		$location.path('/viewReleases/viewRelease/'+ releasePackageId);
	};

	$scope.releasePackageOrderBy = MemberPackageService.orderByJustName;

	var releasePackages = releasePackagesQueryResult;
    $scope.releasePackagesByMember = _.chain(releasePackages)
        .filter(PackageUtilsService.isPackagePublished)
        .groupBy(function(value) {return value.member.key;})
        .map(function(packages, memberKey) {
            return {
                member: MemberService.membersByKey[memberKey],
                packages: PackageUtilsService.releasePackageSort(packages)};})
        .value();
    if(Session.member != null)
    {
        var memberRelease = _.find($scope.releasePackagesByMember, function(item)
        {
            if(Session.member.key != null && Session.member.key != 'undefined' && Session.member.key != "IHTSDO")
            {
                if(item.member.key == Session.member.key)
                {
                    return 1;
                }
            }
            else{return 0};
        });
        _.each($scope.releasePackagesByMember, function(item)
        {
            if(item.member.key == "IHTSDO" && memberRelease != null && memberRelease != 'undefined'){
                _.each(memberRelease.packages, function(releasePackage){
                    item.packages.push(releasePackage);
                });
                item.packages = PackageUtilsService.releasePackageSort(item.packages);
            }
        });
    }

    $scope.alphaReleasePackagesByMember = _.chain(releasePackages)
		.reject(PackageUtilsService.isPackageOffline)
		.reject(PackageUtilsService.isPackagePublished)
        .groupBy(function(value) {return value.member.key;})
        .map(function(packages, memberKey) {
            return {
                member: MemberService.membersByKey[memberKey],
                packages: PackageUtilsService.releasePackageSort(packages)};})
        .value();

    if(Session.member != null)
    {
        var alphamemberRelease = _.find($scope.alphaReleasePackagesByMember, function(item)
        {
            if(Session.member.key != null && Session.member.key != 'undefined' && Session.member.key != "IHTSDO")
            {
                if(item.member.key == Session.member.key)
                {
                    return 1;
                }
            }
            else{return 0};
        });
        _.each($scope.alphaReleasePackagesByMember, function(item)
        {
            if(item.member.key == "IHTSDO" && alphamemberRelease != null && alphamemberRelease != 'undefined'){
                _.each(alphamemberRelease.packages, function(releasePackage){
                    item.packages.push(releasePackage);
                });
                item.packages = PackageUtilsService.releasePackageSort(item.packages);
            }
        });
    }

}]);
