'use strict';

angular.module('MLDS').controller('EditLicenceController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$scope.member = member;
		
		$scope.memberLicenceForm = {};
		$scope.submitStatus = {notSubmitted: true};
		
		$scope.memberLicenceForm.submit = function submit() {
			$scope.alerts = [];
            var file = $scope.memberLicenceForm.file;
            $scope.submitStatus = {submitting: true};
			
			MemberService.updateMemberLicence(member.key, file)
				.then(function(result) {
	        		$scope.alerts.push({type: 'success', msg: 'New licence has been uploaded.'});
	        		$scope.submitStatus = {submitSuccessful: true};
	        	})
				["catch"](function(message) {
					$log.log(message);
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later. ['+ message.statusText+']'});
					$scope.submitStatus = {notSubmitted: true};
				});
		};
		
		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		  };
    }]);

