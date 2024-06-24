'use strict';

angular.module('MLDS')
    .controller('ViewReleaseController',
    		['$scope', '$http', '$routeParams', '$location', 'ServicesUtils','ServicesBundle',
          function($scope, $http, $routeParams, $location, ServicesUtils, ServicesBundle) {

	let releasePackageId = $routeParams.releasePackageId && parseInt($routeParams.releasePackageId, 10);

	$scope.releaseVersions = {
			online: [],
			offline: [],
			alphabeta: []
		};
	$scope.releasePackage = $scope.releasePackage || {releaseVersions:[]};

	$scope.viewLicense = function (memberKey) {
		ServicesBundle.MemberService.getMemberLicense(memberKey);
	};

	$scope.isMembershipInGoodStanding = false;
	$scope.isMembershipApproved = false;
	$scope.isMembershipIncomplete = false;
	$scope.isMembershipUnstarted = false;

	$scope.utils = ServicesBundle.PackageUtilsService;

	$scope.$watch('releasePackage', function(newValue, oldValue) {
		$scope.releaseVersions = $scope.utils.updateVersionsLists(newValue);
	});

	let getLatestMatchingMemberApplication = function getStatusOfLatestMatchingMemberApplication(releasePackage) {
		return _.chain(ServicesBundle.UserAffiliateService.affiliate.applications)
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

	let initReleasePackageState = function initReleasePackageState(releasePackage) {
		ServicesBundle.UserAffiliateService.promise.then(function() {
			$scope.isAccountDeactivated = ServicesBundle.StandingStateUtils.isDeactivated(ServicesBundle.UserAffiliateService.affiliate.standingState);
			$scope.isPendingInvoice = ServicesBundle.StandingStateUtils.isPendingInvoice(ServicesBundle.UserAffiliateService.affiliate.standingState);
			$scope.isMembershipApproved = ServicesBundle.UserAffiliateService.isMembershipApproved(releasePackage.member);
			$scope.isMembershipInGoodStanding = $scope.isMembershipApproved && !$scope.isAccountDeactivated && !$scope.isPendingInvoice;
			$scope.isMembershipIncomplete = ServicesBundle.UserAffiliateService.isMembershipIncomplete(releasePackage.member);
			$scope.isMembershipUnstarted = ServicesBundle.UserAffiliateService.isMembershipNotStarted(releasePackage.member);
			$scope.isPrimaryApplicationApproved = ServicesBundle.ApplicationUtilsService.isApplicationApproved(ServicesBundle.UserAffiliateService.affiliate.application);
			$scope.isPrimaryApplicationWaitingForApplicant = ServicesBundle.ApplicationUtilsService.isApplicationWaitingForApplicant(ServicesBundle.UserAffiliateService.affiliate.application);
			$scope.matchingExtensionApplication = getLatestMatchingMemberApplication(releasePackage);
			$scope.isApplicationWaitingForApplicant = ServicesBundle.ApplicationUtilsService.isApplicationWaitingForApplicant($scope.matchingExtensionApplication);
			$scope.isIHTSDOPackage = ServicesBundle.MemberService.isIhtsdoMember(releasePackage.member);
		});
	};

	let setReleasePackage = function setReleasePackage(releasePackage) {
		$scope.releasePackage = releasePackage;
		initReleasePackageState(releasePackage);
	};

	let loadReleasePackage = function loadReleasePackage() {
		if ($scope.releasePackage?.releasePackageId) {
			// Already initialized as sub-controller
			initReleasePackageState($scope.releasePackage);
		} else if (releasePackageId) {
			// Main controller
			ServicesBundle.PackagesService.get({releasePackageId: releasePackageId})
			.$promise.then(function(result) {
				setReleasePackage(result);
				})
			["catch"](function(message) {
				ServicesUtils.$log.log('ReleasePackage not found');
				$scope.goToViewPackages();
			});
		} else {
			$scope.goToViewPackages();
		}
	};

	loadReleasePackage();

	$scope.goToViewPackages = function goToViewPackages() {
		$location.path('/viewReleases');
	};

	$scope.viewReleaseLicense = function() {
		ServicesBundle.ReleasePackageService.getReleaseLicense(releasePackageId);
	};

    $scope.downloadReleaseFile = function(downloadUrl) {
        let checkUrl = downloadUrl.replace('/download', '/check');
        $http.get(checkUrl).then(function(response) {
            let isIhtsdoPresent = response.data === "true";
            let modalTemplateUrl = isIhtsdoPresent
                ? 'views/user/reviewReleaseLicenseModal.html'
                : 'views/user/reviewReleaseLicenseWithDisclaimerModal.html';
            let modalInstance = ServicesUtils.$modal.open({
                templateUrl: modalTemplateUrl,
                size: 'lg',
                scope: $scope
            });
                modalInstance.result.then(function() {
                ServicesUtils.$window.open(downloadUrl, '_blank');
            });
        });
    };

}]);


