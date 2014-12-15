'use strict';

angular.module('MLDS').controller('EditMemberController',
		['$scope', '$log', '$modal', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modal, MemberService, Session, member) {
		$scope.member = member;
		
		$scope.memberBrandForm = {};
		$scope.submitStatus = {notSubmitted: true};
		
		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		  };
		  
		  
		  $scope.memberBrandForm.submit = function submit() {
				$scope.alerts = [];
	            var file = $scope.memberBrandForm.file;
	            $scope.submitStatus = {submitting: true};
	
	            MemberService.updateMemberBrand(member.key, file, member.name || '')
				.then(function(result) {
					var message = file ? 'New logo has been uploaded.' : 'Member name updated.';
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

