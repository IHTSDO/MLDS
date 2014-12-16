'use strict';

angular.module('MLDS').controller('EditLicenseController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$scope.currentMember = angular.copy(member);
		$scope.member = member;
		
		$scope.memberLicenseForm = {};
		$scope.submitStatus = {notSubmitted: true};
		
		$scope.memberLicenseForm.submit = function submit() {
            var file = $scope.memberLicenseForm.file;
			
            if (file) {
            	confirmUpload();
            } else {
            	updateMemberLicense();
            }
		};
		
		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		  };
		  
		  function confirmUpload() {
				var modalInstance = $modal.open({
	                templateUrl: 'views/admin/editLicenseConfirmModal.html',
	                controller: 'EditLicenseConfirmController',
	                scope: $scope,
	                backdrop: 'static',
	                resolve: {
	                  currentMember: function() {
	                  	return $scope.currentMember;
	                  }
	                }
	              });
				modalInstance.result.then(updateMemberLicense);

		  }
		  
		  function updateMemberLicense() {
				$scope.alerts = [];
	            var file = $scope.memberLicenseForm.file;
	            $scope.submitStatus = {submitting: true};
	
	            MemberService.updateMemberLicense(member.key, file, member.licenseName || '', member.licenseVersion || '')
				.then(function(result) {
					var message = file ? 'New license has been uploaded.' : 'License name and version updated.';
	        		$scope.alerts.push({type: 'success', msg: message});
	        		$scope.submitStatus = {submitSuccessful: true};
	        	})
				["catch"](function(message) {
					$log.log(message);
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later. ['+ message.statusText+']'});
					$scope.submitStatus = {notSubmitted: true};
				});
		  }
    }]);

