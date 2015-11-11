'use strict';

angular.module('MLDS').controller('ReleaseManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location', '$translate', 'PackageUtilsService', 'Session', 'MemberService',
    function ($scope, $log, $modal, PackagesService, $location, $translate, PackageUtilsService, Session, MemberService) {
			
		$scope.utils = PackageUtilsService;
		$scope.isAdmin = Session.isAdmin();
		
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
			var packages = $scope.packages;
			if (!packages.$resolved) {
				return;
			}
			
			var memberFiltered = _.chain(packages).filter(function(p){ return PackageUtilsService.showAllMembers || PackageUtilsService.isReleasePackageMatchingMember(p); });

		    $scope.packagesByMember = _.chain(memberFiltered)
		        .groupBy(function(value) {return value.member.key;})
		        .map(function(packages, memberKey) {
		        	var onlinePackages = PackageUtilsService.releasePackageSort(
		        			_.filter(packages, PackageUtilsService.isPackagePublished));
		        	var offlinePackages = _.chain(packages)
			        	.reject(PackageUtilsService.isPackagePublished)
				        .sortBy('createdAt')
				        .value();
		            return {
		                member: MemberService.membersByKey[memberKey], 
		                onlinePackages: onlinePackages,
		                offlinePackages: offlinePackages
		             };})
		         .sortBy(function(memberEntry) {return memberEntry.member.key === 'IHTSDO' ? '!IHTSDO' : $translate.instant('global.member.'+memberEntry.member.key);})
		        .value();

		    fixReleasePackagesWithoutPriority(memberFiltered);
		}
		
		function fixReleasePackagesWithoutPriority(memberFiltered) {
		    var firstMissing = _.chain(memberFiltered)
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
		
		$scope.$watch('utils.showAllMembers', extractPackages);
				
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
            var modalInstance = $modal.open({
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
            var modalInstance = $modal.open({
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
        	var i = memberEntry.onlinePackages.indexOf(p);
        	if (i > 0) {
        		return memberEntry.onlinePackages[i - 1];
        	} else {
        		return null;
        	}
        }
        
        function findLowerPriorityPackage(memberEntry, p) {
        	var i = memberEntry.onlinePackages.indexOf(p);
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
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
			});
		}
        
        $scope.canPromotePackage = function canPromotePackage(memberEntry, p) {
        	return findHigherPriorityPackage(memberEntry, p);
        };
        
        $scope.promotePackage = function promotePackage(memberEntry, p) {
    		var higherPriority = findHigherPriorityPackage(memberEntry, p);
    		exchangePackages(p, higherPriority);
        };
        
        $scope.canDemotePackage = function canDemotePackage(memberEntry, p) {
        	return findLowerPriorityPackage(memberEntry, p);
        };
        
        $scope.demotePackage = function demotePackage(memberEntry, p) {
        	var lower = findLowerPriorityPackage(memberEntry, p);
        	exchangePackages(lower, p);
        };
        
        $scope.promoteMemberPackagesChanged = function promoteMemberPackagesChanged(member) {
			MemberService.updateMember(member)
			.then(function(result) {
	        	// updated
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
			});
        }; 

    }]);

