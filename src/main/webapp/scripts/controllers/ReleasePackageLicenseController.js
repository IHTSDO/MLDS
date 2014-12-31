'use strict';

angular.module('MLDS').controller('ReleasePackageLicenseController',
		['$scope', '$log', '$modal', '$http', 'Session', 'ReleasePackageService', 'releasePackage',
    function ($scope, $log, $modal, $http, Session, ReleasePackageService, releasePackage) {
		$scope.releasePackage = releasePackage;
		
		$scope.licenseForm = {};
		$scope.submitStatus = {notSubmitted: true};
		
		$scope.licenseForm.submit = function submit() {
            var file = $scope.licenseForm.file;
			
            if (file) {
            	confirmUpload();
            }
		};
		
		$scope.closeAlert = function(index) {
			$scope.alerts.splice(index, 1);
		};
		  
		function confirmUpload() {
			var modalInstance = $modal.open({
	            templateUrl: 'views/admin/editReleaseLicenseConfirmModal.html',
				scope: $scope,
				backdrop: 'static',
	            resolve: {
	              currentMember: function() {
	              	return $scope.currentMember;
	              }
	            }
	          });
			modalInstance.result.then(updateReleasePackageLicense);
		}
		  
		function updateReleasePackageLicense() {
			$scope.alerts = [];
			$scope.submitStatus = {submitting: true};
			
			ReleasePackageService.updateReleaseLicense(releasePackage.releasePackageId, $scope.licenseForm.file)
				.then(function(result) {
	        		$scope.alerts.push({type: 'success', msg: 'New license has been uploaded.'});
	        		$scope.submitStatus = {submitSuccessful: true};
		        })
		        ["catch"](function(message) {
					$log.log(message);
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later. ['+ message.statusText+']'});
					$scope.submitStatus = {notSubmitted: true};
				});
		}
		
    }]);

