'use strict';

angular.module('MLDS').controller('EditLicenceController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$scope.currentMember = angular.copy(member);
		$scope.member = member;
		
		$scope.memberLicenceForm = {};
		$scope.submitStatus = {notSubmitted: true};
		
		$scope.memberLicenceForm.submit = function submit() {
            var file = $scope.memberLicenceForm.file;
			
            if (file) {
            	confirmUpload();
            } else {
            	updateMemberLicence();
            }
		};
		
		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		  };
		  
		  function confirmUpload() {
				var modalInstance = $modal.open({
	                templateUrl: 'views/admin/editLicenceConfirmModal.html',
	                controller: 'EditLicenceConfirmController',
	                scope: $scope,
	                backdrop: 'static',
	                resolve: {
	                  currentMember: function() {
	                  	return $scope.currentMember;
	                  }
	                }
	              });
				modalInstance.result.then(updateMemberLicence);

		  }
		  
		  function updateMemberLicence() {
				$scope.alerts = [];
	            var file = $scope.memberLicenceForm.file;
	            $scope.submitStatus = {submitting: true};
	
	            MemberService.updateMemberLicence(member.key, file, member.licenceName || '', member.licenceVersion || '')
				.then(function(result) {
					var message = file ? 'New licence has been uploaded.' : 'Licence name and version updated.';
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

