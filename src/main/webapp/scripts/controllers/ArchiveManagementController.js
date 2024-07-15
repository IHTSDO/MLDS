'use strict';

angular.module('MLDS').controller('ArchiveManagementController',
		['$scope', '$location', '$translate', 'PackageUtilsService', 'Session', 'SessionStateService', 'ServicesBundle',
    function ($scope, $location, $translate, PackageUtilsService, Session, SessionStateService, ServicesBundle ) {

		$scope.utils = PackageUtilsService;
		$scope.isAdmin = Session.isAdmin();

		$scope.releaseManagementFilter = SessionStateService.sessionState.releaseManagementFilter;

		$scope.alerts = [];

		$scope.memberPackages = [];
		$scope.packages = [];

		$scope.packagesByMember = [];
		$scope.archivePackages = [];

		function reloadPackages() {
			$scope.packages = ServicesBundle.PackagesService.query();
			$scope.packages.$promise.then(extractPackages);
		}
		function extractPackages() {
			let packages = $scope.packages;
			if (!packages.$resolved) {
				return;
			}

			let memberFiltered = _.chain(packages).filter(function(p){ return $scope.releaseManagementFilter.showAllMembers || PackageUtilsService.isReleasePackageMatchingMember(p); });

		    $scope.packagesByMember = _.chain(memberFiltered)
		        .groupBy(function(value) {return value.member.key;})
		        .map(function(memberPackages, memberKey) {
				    let archivePackages = _.chain(memberPackages)
                    .filter(function(releasePackage) {
                       return _.some(releasePackage.releaseVersions, 'archive');
                    })
                    .value();
		            return {
		                member: ServicesBundle.MemberService.membersByKey[memberKey],
		                archivePackages: archivePackages
		             };})
		         .sortBy(function(memberEntry) {return memberEntry.member.key === 'IHTSDO' ? '!IHTSDO' : $translate.instant('global.member.'+memberEntry.member.key);})
		        .value();
		}

		$scope.$watch('releaseManagementFilter.showAllMembers', extractPackages);

		reloadPackages();

        $scope.goToArchivePackage = function(packageEntity) {
            $location.path('/archiveReleases/archivePackage/'+encodeURIComponent(packageEntity.releasePackageId));
        };

    }]);

