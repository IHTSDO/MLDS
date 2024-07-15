'use strict';

angular.module('MLDS').controller('ReleaseManagementController',
		['$scope', '$log', '$modal', 'PackagesService', '$location', '$translate', 'PackageUtilsService', 'Session', 'MemberService', 'SessionStateService',
    function ($scope, $log, $modal, PackagesService, $location, $translate, PackageUtilsService, Session, MemberService, SessionStateService) {

		$scope.utils = PackageUtilsService;
		$scope.isAdmin = Session.isAdmin();

		$scope.releaseManagementFilter = SessionStateService.sessionState.releaseManagementFilter;

		$scope.alerts = [];

		$scope.memberPackages = [];
		$scope.packages = [];
		$scope.onlineMemberPackages = [];

		$scope.packagesByMember = [];

		function reloadPackages() {
			$scope.packages = PackagesService.query();
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
		        	let onlinePackages = PackageUtilsService.releasePackageSort(
		        			_.filter(memberPackages, PackageUtilsService.isPackagePublished));
		        	let alphabetaPackages = _.chain(memberPackages)
			        	.reject(PackageUtilsService.isPackageOffline) //offline
			        	.reject(PackageUtilsService.isPackagePublished) //online
			        	.reject(PackageUtilsService.isPackageEmpty) //versions
				        .sortBy('createdAt')
				        .value();
                    let offlinePackages = _.chain(memberPackages)
			        	.reject(PackageUtilsService.isPackageNotPublished)
			        	.reject(PackageUtilsService.isPackagePublished)
			        	.reject(PackageUtilsService.isPackageFullyArchived)
				        .sortBy('createdAt')
				        .value();
		            return {
		                member: MemberService.membersByKey[memberKey],
		                onlinePackages: onlinePackages,
		                alphabetaPackages: alphabetaPackages,
		                offlinePackages: offlinePackages
		             };})
		         .sortBy(function(memberEntry) {return memberEntry.member.key === 'IHTSDO' ? '!IHTSDO' : $translate.instant('global.member.'+memberEntry.member.key);})
		        .value();

		    fixReleasePackagesWithoutPriority(memberFiltered);
		}

		function fixReleasePackagesWithoutPriority(memberFiltered) {
		    let firstMissing = _.chain(memberFiltered)
		    	.filter(PackageUtilsService.isPackagePublished)
		    	.sortBy(PackageUtilsService.getLatestPublishedDate)
		    	.filter(function(p) {return p.priority === -1 || p.priority === null;})
		    	.first()
		    	.value();
			if (firstMissing) {
				$log.log('Found release package without priority', firstMissing);
				updatePackagePriority(firstMissing, 0);
			}
		}

		$scope.$watch('releaseManagementFilter.showAllMembers', extractPackages);

		reloadPackages();

		$scope.addReleasePackage = function() {
			var modalInstance = $modal.open({
				templateUrl: 'views/admin/addReleaseModal.html',
				controller: 'AddReleaseModalController',
				size:'lg',
				backdrop: 'static',
				resolve: {
				}
			});
		};

        $scope.editReleasePackage = function editReleasePackage(releasePackage) {
            let modalInstance = $modal.open({
                  templateUrl: 'views/admin/editPackageModal.html',
                  controller: 'EditPackageModalController',
                  scope: $scope,
                  size: 'lg',
                  backdrop: 'static',
                  resolve: {
                    releasePackage: function() {
                    	//FIXME not sure about copy - needed to support modal cancel or network failure
                    	return angular.copy(releasePackage);
                    }
                  }
                });
            modalInstance.result.then(function(result) {
            	reloadPackages();
            });
        };

        $scope.goToPackage = function(packageEntity) {
        	$location.path('/releaseManagement/release/'+encodeURIComponent(packageEntity.releasePackageId));
        };

        $scope.deleteReleasePackage = function(releasePackage) {
            let modalInstance = $modal.open({
                templateUrl: 'views/admin/deletePackageModal.html',
                controller: 'DeletePackageModalController',
                scope: $scope,
                size: 'sm',
                backdrop: 'static',
                resolve: {
                  releasePackage: function() {
                  	return releasePackage;
                  }
                }
              });
            modalInstance.result.then(function(result) {
            	reloadPackages();
            });
        };

        $scope.isEditableReleasePackage = function(p) {
        	PackageUtilsService.isEditableReleasePackage(p);
        };

        function findHigherPriorityPackage(memberEntry, p) {
        	let i = memberEntry.onlinePackages.indexOf(p);
        	if (i > 0) {
        		return memberEntry.onlinePackages[i - 1];
        	} else {
        		return null;
        	}
        }

        function findLowerPriorityPackage(memberEntry, p) {
        	let i = memberEntry.onlinePackages.indexOf(p);
        	if (i !== -1 && i < memberEntry.onlinePackages.length -1) {
        		return memberEntry.onlinePackages[i + 1];
        	} else {
        		return null;
        	}
        }

        function exchangePackages(p, replace) {
        	updatePackagePriority(p, replace.priority);
        };

        function updatePackagePriority(p, priority) {
        	$scope.alerts.splice(0, $scope.alerts.length);
        	p.priority = priority;
			PackagesService.update(p)
			.$promise.then(function(result) {
	        	reloadPackages();
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure [12]: please try again later.'});
			});
		}

        $scope.canPromotePackage = function canPromotePackage(memberEntry, p) {
        	return findHigherPriorityPackage(memberEntry, p);
        };

        $scope.promotePackage = function promotePackage(memberEntry, p) {
    		let higherPriority = findHigherPriorityPackage(memberEntry, p);
    		exchangePackages(p, higherPriority);
        };

        $scope.canDemotePackage = function canDemotePackage(memberEntry, p) {
        	return findLowerPriorityPackage(memberEntry, p);
        };

        $scope.demotePackage = function demotePackage(memberEntry, p) {
        	let lower = findLowerPriorityPackage(memberEntry, p);
        	exchangePackages(lower, p);
        };

        $scope.promoteMemberPackagesChanged = function promoteMemberPackagesChanged(member) {
			MemberService.updateMember(member)
			.then(function(result) {
	        	// updated
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure [45]: please try again later.'});
			});
        };

    }]);

