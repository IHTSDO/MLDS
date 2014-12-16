'use strict';

angular.module('MLDS')
    .controller('ViewReleaseController', 
    		['$scope', '$routeParams', 'PackagesService', 'PackageUtilsService', '$location', '$log', 'UserAffiliateService', 'ApplicationUtilsService', 'MemberService', 'StandingStateUtils',
          function($scope, $routeParams, PackagesService, PackageUtilsService, $location, $log, UserAffiliateService, ApplicationUtilsService, MemberService, StandingStateUtils){
    	
	var releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);
	
	$scope.releaseVersions = {
			online: [],
			offline: []
		};
	$scope.releasePackage = $scope.releasePackage || {releaseVersions:[]};
	
	$scope.viewLicense = function (memberKey) {
		MemberService.getMemberLicense(memberKey);
	};
	
	$scope.isMembershipInGoodStanding = false;
	$scope.isMembershipApproved = false;
	$scope.isMembershipIncomplete = false;
	$scope.isMembershipUnstarted = false;
	
	$scope.utils = PackageUtilsService;
	
	$scope.$watch('releasePackage', function(newValue, oldValue) {
		$scope.releaseVersions = $scope.utils.updateVersionsLists(newValue);
	});
	
	var getLatestMatchingMemberApplication = function getStatusOfLatestMatchingMemberApplication(releasePackage) {
		return _.chain(UserAffiliateService.affiliate.applications)
				.filter(function(application){return application.member.key === releasePackage.member.key;})
				.max(function(application){return new Date(application.submittedAt);})
				.value();
	};
	
	$scope.isPrimaryApplicationApproved = false;
	$scope.isIHTSDOPackage = false;
	$scope.isApplicationWaitingForApplicant = false;
	$scope.matchingExtensionApplication = {};
	
	$scope.goToExtensionApplication = function goToExtensionApplication() {
		$location.path('/extensionApplication/'+$scope.matchingExtensionApplication.applicationId);
	};
		
	var initReleasePackageState = function initReleasePackageState(releasePackage) {
		UserAffiliateService.promise.then(function() {
			$scope.isAccountDeactivated = StandingStateUtils.isDeactivated(UserAffiliateService.affiliate.standingState);
			$scope.isPendingInvoice = StandingStateUtils.isPendingInvoice(UserAffiliateService.affiliate.standingState);
			$scope.isMembershipApproved = UserAffiliateService.isMembershipApproved(releasePackage.member);
			$scope.isMembershipInGoodStanding = $scope.isMembershipApproved && !$scope.isAccountDeactivated && !$scope.isPendingInvoice;
			$scope.isMembershipIncomplete = UserAffiliateService.isMembershipIncomplete(releasePackage.member);
			$scope.isMembershipUnstarted = UserAffiliateService.isMembershipNotStarted(releasePackage.member);
			$scope.isPrimaryApplicationApproved = ApplicationUtilsService.isApplicationApproved(UserAffiliateService.affiliate.application);
			$scope.isPrimaryApplicationWaitingForApplicant = ApplicationUtilsService.isApplicationWaitingForApplicant(UserAffiliateService.affiliate.application);
			$scope.matchingExtensionApplication = getLatestMatchingMemberApplication(releasePackage);
			$scope.isApplicationWaitingForApplicant = ApplicationUtilsService.isApplicationWaitingForApplicant($scope.matchingExtensionApplication);
			$scope.isIHTSDOPackage = MemberService.isIhtsdoMember(releasePackage.member);
		});
	};

	var setReleasePackage = function setReleasePackage(releasePackage) {
		$scope.releasePackage = releasePackage;
		//$log.log('setReleasePackage', releasePackage);
		initReleasePackageState(releasePackage);
	};

	var loadReleasePackage = function loadReleasePackage() {
		if ($scope.releasePackage && $scope.releasePackage.releasePackageId) {
			// Already initialized as sub-controller
			initReleasePackageState($scope.releasePackage);
		} else if (releasePackageId) {
			// Main controller
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				setReleasePackage(result);
				})
			["catch"](function(message) {
				//FIXME how to handle errors + not present 
				$log.log('ReleasePackage not found');
				$scope.goToViewPackages();
			});
		} else {
			$scope.goToViewPackages();
		};
	};

	loadReleasePackage();

	$scope.goToViewPackages = function goToViewPackages() {
		$location.path('/viewReleases');
	};
		
}]);


