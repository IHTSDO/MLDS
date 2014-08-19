'use strict';

angular.module('MLDS').controller('EditLicenseController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$scope.member = member;
		
		$scope.memberLicenseForm = {};
		$scope.submitStatus = {notSubmitted: true};
		
		$scope.memberLicenseForm.submit = function submit() {
			$scope.alerts = [];
            var file = $scope.memberLicenseForm.file;
            $scope.submitStatus = {submitting: true};
			
			MemberService.updateMemberLicense(member.key, file)
				.then(function(result) {
	        		$scope.alerts.push({type: 'success', msg: 'New license has been uploaded.'});
	        		$scope.submitStatus = {submitSuccessful: true};
	        	})
				["catch"](function(message) {
					$log.log(message);
					$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later. ['+ message.statusText+']'});
					$scope.submitStatus = {notSubmitted: true};
				});
		};
		
		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		  };
    }]);

