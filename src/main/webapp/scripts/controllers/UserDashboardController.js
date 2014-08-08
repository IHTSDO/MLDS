'use strict';

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', 'AffiliateService', 'Session', 'ApplicationUtilsService', 'UsageReportsService', 'UserAffiliateService', 'PackageUtilsService', 'MemberService', 'PackagesService',
          function ($scope, $log, $location, AffiliateService, Session, ApplicationUtilsService, UsageReportsService, UserAffiliateService, PackageUtilsService, MemberService, PackagesService) {
        	
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

        	$scope.packageUtils = PackageUtilsService;
        	
        	$scope.affiliate = UserAffiliateService.affiliate;
        	$scope.approvedReleasePackagesByMember = [];
        	$scope.notApprovedReleasePackagesByMember = [];


        	UserAffiliateService.promise.then(function() {
        		if (ApplicationUtilsService.isApplicationWaitingForApplicant($scope.affiliate.application)) {
        			$location.path('/affiliateRegistration');
        			return;
        		}
        		
        		$scope.affiliate.commercialUsages.sort(function(a, b) {
        			if (a.startDate && b.startDate) {
        				return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
        			} else if (a.startDate) {
        				return 1;
        			} else {
        				return -1;
        			}
        		});
        	});

        	loadReleasePackages();
        	
        	$scope.usageReportsUtils = UsageReportsService;
        	
        	$scope.isApplicationPending = function(application) {
        		return ApplicationUtilsService.isApplicationPending(application);
        	};
        	
        	$scope.isApplicationWaitingForApplicant = function(application) {
        		return ApplicationUtilsService.isApplicationWaitingForApplicant(application);
        	};
        	
        	$scope.isApplicationApproved = function(application) {
        		return ApplicationUtilsService.isApplicationApproved(application);
        	};

        	function loadReleasePackages() {
        		PackagesService.query().$promise
        			.then(function(releasePackages) {
        				var releasePackagesByMember = _.chain(releasePackages)
        					.filter(PackageUtilsService.isPackagePublished)
        					.groupBy(function(value) {return value.member.key;})
        					.map(function(packages, memberKey) {
        						return {
        							member: MemberService.membersByKey[memberKey], 
        							packages: packages};})
        					.value();
        				$scope.approvedReleasePackagesByMember = _.filter(releasePackagesByMember, function(memberRelease) {return UserAffiliateService.isMembershipApproved(memberRelease.member);});
        				$scope.notApprovedReleasePackagesByMember = _.filter(releasePackagesByMember, function(memberRelease) {return ! UserAffiliateService.isMembershipApproved(memberRelease.member);});
        			})
        			["catch"](function(message) {
        				//FIXME failed to load release packages
        				$log.log('Failed to load release packages');
        			});
        	}

        }
    ]);
