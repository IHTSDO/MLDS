'use strict';

angular.module('MLDS').controller('ReleasePackageLicenseController',
		['$scope', '$log', '$modal', '$http', 'Session', 'releasePackage',
    function ($scope, $log, $modal, $http, Session, releasePackage) {
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
            var file = $scope.licenseForm.file;
            $scope.submitStatus = {submitting: true};

            var formData = new FormData();
	        formData.append('file', file);
	        
	        console.log(releasePackage);
	        
	        $http.post('/app/rest/releasePackages/' + encodeURIComponent(releasePackage.releasePackageId) + '/license', formData, {
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined}
	        }).then(function(result) {
        		$scope.alerts.push({type: 'success', msg: 'New license has been uploaded.'});
        		$scope.submitStatus = {submitSuccessful: true};
	        })
	        ["catch"](function(message) {
				$log.log(message);
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later. ['+ message.statusText+']'});
				$scope.submitStatus = {notSubmitted: true};
			});;
		  }
    }]);

