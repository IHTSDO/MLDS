'use strict';

angular.module('MLDS').controller('ReleaseManagementController', 
		['$scope', '$log', '$modal', 'PackagesService', '$location', 'PackageUtilsService', 'Session', 'MemberService',
    function ($scope, $log, $modal, PackagesService, $location, PackageUtilsService, Session, MemberService) {
			
		$scope.utils = PackageUtilsService;
		$scope.isAdmin = Session.isAdmin();
		
		$scope.alerts = [];
		
		$scope.packages = [];
		$scope.onlineMemberPackages = [];
		
		$scope.member = MemberService.membersByKey[Session.member.key];
		
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
			
			$scope.onlinePackages = memberFiltered
				.filter(PackageUtilsService.isPackagePublished)
				.sortBy(PackageUtilsService.getLatestPublishedDate)
				.sortBy('priority')
				.reverse()
				.value();
			$scope.offinePackages = memberFiltered
				.reject(PackageUtilsService.isPackagePublished)
				.sortBy('createdAt')
				.value();

			$scope.onlineMemberPackages = _.filter($scope.onlinePackages, PackageUtilsService.isReleasePackageMatchingMember);

			var missingPriorities = _.filter($scope.onlineMemberPackages, function(p) {return p.priority == -1;});
			var firstMissing = _.first(missingPriorities);
			if (firstMissing) {
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
        
        function findHigherPriorityPackage(p) {
        	var i = $scope.onlineMemberPackages.indexOf(p);
        	if (i > 0) {
        		return $scope.onlineMemberPackages[i - 1];
        	} else {
        		return null;
        	}
        }
        
        function findLowerPriorityPackage(p) {
        	var i = $scope.onlineMemberPackages.indexOf(p);
        	if (i !== -1 && i < $scope.onlineMemberPackages.length -1) {
        		return $scope.onlineMemberPackages[i + 1];
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
        
        $scope.canPromotePackage = function canPromotePackage(p) {
        	return findHigherPriorityPackage(p);
        };
        
        $scope.promotePackage = function promotePackage(p) {
    		var higherPriority = findHigherPriorityPackage(p);
    		exchangePackages(p, higherPriority);
        };
        
        $scope.canDemotePackage = function canDemotePackage(p) {
        	return findLowerPriorityPackage(p);
        };
        
        $scope.demotePackage = function demotePackage(p) {
        	var lower = findLowerPriorityPackage(p);
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

