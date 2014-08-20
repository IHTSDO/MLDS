'use strict';

angular.module('MLDS')
    .controller('ViewPackageController', 
    		['$scope', '$routeParams', 'PackagesService', 'PackageUtilsService', '$location', '$log', 'UserAffiliateService', 'ApplicationUtilsService', 'MemberService',
          function($scope, $routeParams, PackagesService, PackageUtilsService, $location, $log, UserAffiliateService, ApplicationUtilsService, MemberService){
    	
	var releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);
	
	$scope.releaseVersions = {
			online: [],
			offline: []
		};
	$scope.releasePackage = {releaseVersions:[]};
	
	$scope.viewLicense = function (memberKey) {
		MemberService.getMemberLicense(memberKey);
	};
	
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
	
	var setReleasePackage = function setReleasePackage(releasePackage) {
		$scope.releasePackage = releasePackage;
		//$log.log('setReleasePackage', releasePackage);
		UserAffiliateService.promise.then(function() {
			$scope.isMembershipApproved = UserAffiliateService.isMembershipApproved(releasePackage.member);
			$scope.isMembershipIncomplete = UserAffiliateService.isMembershipIncomplete(releasePackage.member);
			$scope.isMembershipUnstarted = UserAffiliateService.isMembershipNotStarted(releasePackage.member);
			$scope.isPrimaryApplicationApproved = ApplicationUtilsService.isApplicationApproved(UserAffiliateService.affiliate.application);
			$scope.matchingExtensionApplication = getLatestMatchingMemberApplication(releasePackage);
			$scope.isApplicationWaitingForApplicant = ApplicationUtilsService.isApplicationWaitingForApplicant($scope.matchingExtensionApplication);
			$scope.isIHTSDOPackage = MemberService.isIhtsdoMember(releasePackage.member);
		});
	};

	var loadReleasePackage = function loadReleasePackage() {
		if (releasePackageId) {
			PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				setReleasePackage(result);
				})
			["catch"](function(message) {
				//FIXME how to handle errors + not present 
				$log.log('ReleasePackage not found');
				$location.path('/viewPackages');
			});
		} else {
			$location.path('/viewPackages');
		};
	};

	loadReleasePackage();

	$scope.goToViewPackages = function goToViewPackages() {
		$location.path('/viewPackages');
	};
		
}]);


