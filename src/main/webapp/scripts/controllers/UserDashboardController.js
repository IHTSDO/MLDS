'use strict';

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$modal', '$location', 'AffiliateService', 'Session', 'ApplicationUtilsService', 'UsageReportsService', 'UserAffiliateService', 'PackageUtilsService', 'MemberService', 'PackagesService', 'StandingStateUtils','MemberPackageService',
          function ($scope, $log, $modal, $location, AffiliateService, Session, ApplicationUtilsService, UsageReportsService, UserAffiliateService, PackageUtilsService, MemberService, PackagesService, StandingStateUtils, MemberPackageService) {
        	
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

        	$scope.packageUtils = PackageUtilsService;
        	$scope.usageReportsUtils = UsageReportsService;
        	$scope.standingStateUtils = StandingStateUtils;
        	
        	$scope.affiliate = UserAffiliateService.affiliate;
        	$scope.approvedReleasePackagesByMember = [];
        	$scope.notApprovedReleasePackagesByMember = [];
        	$scope.showingUserDashboardWidgets = true;

        	$scope.viewLicense = function (memberKey) {
    			MemberService.getMemberLicense(memberKey);
    		};
        	
        	UserAffiliateService.promise.then(function() {
        		loadReleasePackages();
        		
        		if (ApplicationUtilsService.isApplicationWaitingForApplicant(UserAffiliateService.affiliate.application)) {
        			$location.path('/affiliateRegistration');
        			return;
        		}
        	});

        	
        	$scope.isApplicationPending = function(application) {
        		return ApplicationUtilsService.isApplicationPending(application);
        	};
        	
        	$scope.isApplicationWaitingForApplicant = function(application) {
        		return ApplicationUtilsService.isApplicationWaitingForApplicant(application);
        	};
        	
        	$scope.isApplicationApproved = function(application) {
        		return ApplicationUtilsService.isApplicationApproved(application);
        	};
        	
        	// Affiliates under members are shown the IHTSDO International packages as belonging to their member
        	function getEffectivePackageMemberKey(releasePackage) {
        		var packageMemberKey = releasePackage.member.key;
        		if (packageMemberKey == 'IHTSDO') {
        			return UserAffiliateService.affiliate.application.member.key
        		}
        		return packageMemberKey;
        	}
        	
        	function loadReleasePackages() {
        		PackagesService.query().$promise
        			.then(function(releasePackages) {
        				var releasePackagesByMember = _.chain(releasePackages)
        					.filter(PackageUtilsService.isPackagePublished)
        					.groupBy(getEffectivePackageMemberKey)
        					.map(function(packages, memberKey) {
        						return {
        							member: MemberService.membersByKey[memberKey], 
        							packages: packages};})
        					.value();
        				if (StandingStateUtils.isDeactivated($scope.affiliate.standingState)) {
        					$scope.approvedReleasePackagesByMember = [];
        					$scope.notApprovedReleasePackagesByMember = releasePackagesByMember;
        				} else {
	        				$scope.approvedReleasePackagesByMember = _.filter(releasePackagesByMember, function(memberRelease) {return UserAffiliateService.isMembershipApproved(memberRelease.member);});
	        				$scope.notApprovedReleasePackagesByMember = _.filter(releasePackagesByMember, function(memberRelease) {return ! UserAffiliateService.isMembershipApproved(memberRelease.member);});
        				}
        			})
        			["catch"](function(message) {
        				//FIXME failed to load release packages
        				$log.log('Failed to load release packages');
        			});
        	}
        	
        	$scope.orderByApprovalState = function orderByApprovalState(application) {
        		return ApplicationUtilsService.isApplicationApproved(application);
        	};
        	
        	$scope.orderByApplicationType = function orderByApplicationType(application) {
        		return !ApplicationUtilsService.isPrimaryApplication(application);
        	};
        	
        	$scope.viewUsageReports = function() {
        		$location.path('/usageReports');        		
        	};
        	
        	$scope.releasePackageOrderBy = MemberPackageService.orderBy;
        	
        	$scope.goToViewPackagePage = function goToViewPackagePage(releasePackageId) {
        		$location.path('/viewReleases/viewRelease/'+ releasePackageId);
        	};
        	
        	$scope.viewApplication = function viewApplication(application) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/applicationSummaryModal.html',
        			controller: 'ApplicationSummaryModalController',
        			size:'lg',
        			resolve: {
        				application: function() {
        					return application;
        				},
        				audits: function() {
        					return [];
        				}
        			}
        		});
			};

        }
    ]);
