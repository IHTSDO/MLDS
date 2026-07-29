'use strict';

angular.module('MLDS').controller('AddEditReleaseVersionModalController', 
		['$scope', '$log', '$modalInstance', 'PackagesService', 'releasePackage', 'ReleaseVersionsService', 'releaseVersion',
		 function($scope, $log,  $modalInstance, PackagesService, releasePackage, ReleaseVersionsService, releaseVersion) {
	
	var isNewObject = !(releaseVersion.releaseVersionId);
			
	$scope.isNewObject = isNewObject;
	$scope.releasePackage = releasePackage;
	$scope.releaseVersion = releaseVersion;
	
	// SNOMED CT version URI
	var SCT_VERSION_URI_RE = /^http:\/\/snomed\.info\/x?sct\/[0-9]{6,18}\/version\/(19|20)[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$/;

	var ANY_RE = /^.*$/;

	function isOtherPackage() {
		return $scope.releaseVersion && $scope.releaseVersion.packageType === 'OTHER';
	};

	// NB: these return shared constants, never freshly-constructed values, so the
	// ngPattern and interpolation watchers stay reference-stable across digests.
	$scope.versionUriPattern = function() {
		return isOtherPackage() ? ABSOLUTE_URI_RE : SCT_VERSION_URI_RE;
	};

	$scope.versionUriPlaceholder = function() {
		return isOtherPackage()
			? 'Enter release version'
			: 'http://snomed.info/sct/<moduleId>/version/<YYYYMMDD>';
	};

	$scope.versionUriError = function() {
		return isOtherPackage()
			? ''
			: 'Must be a valid SNOMED CT version URI, e.g. http://snomed.info/sct/32506021000036107/version/20260601 (scheme must be http, not https)';
	};

	$scope.submitAttempted = false;
	$scope.submitting = false;
	$scope.alerts = [];
	
	function serializeDate(date) {
		if (date) {
			return moment(date).format('YYYY-MM-DD');
		} else {
			return null;
		}
	};

	$scope.dateOpen = {};
	
	$scope.openDate = function($event, name) {
		$event.preventDefault();
		$event.stopPropagation();
		$scope.dateOpen[name] = true;
		$log.log('openDate scope', name, $scope.dateOpen[name], $scope);
	}
	
	$scope.ok = function(form) {
		$scope.submitAttempted = true;
		$scope.submitting = true;
		$scope.alerts.splice(0, $scope.alerts.length);
		
		$scope.releaseVersion.publishedAt = serializeDate($scope.releaseVersion.publishedAt);
		
		ReleaseVersionsService[isNewObject?'save':'update']({releasePackageId : releasePackage.releasePackageId}, $scope.releaseVersion)
			.$promise.then(function(result) {
				$modalInstance.close(result);
			})
			["catch"](function(message) {
				$scope.alerts.push({type: 'danger', msg: 'Network request failure [10]: please try again later.'});
				$scope.submitting = false;
			});
	};

}]);
