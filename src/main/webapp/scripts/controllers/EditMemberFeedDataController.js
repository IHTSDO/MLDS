'use strict';

angular.module('MLDS').controller('EditMemberFeedDataController',
		['$scope', '$log', '$modalInstance', 'MemberService', 'Session', 'member',
    function ($scope, $log, $modalInstance, MemberService, Session, member) {
		$scope.member = member;

		$scope.submitStatus = {notSubmitted: true};

		$scope.closeAlert = function(index) {
		    $scope.alerts.splice(index, 1);
		  };

		$scope.ok = function ok() {
			$scope.alerts = [];
            $scope.submitStatus = {submitting: true};

            MemberService.updateMemberFeedData(member.key, member.memberOrgName, member.memberOrgURL, member.contactEmail)
			.then(function(result) {
				$modalInstance.close();
        	})
			["catch"](function(message) {
				$log.log(message);
				$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later. ['+ message.statusText+']'});
				$scope.submitStatus = {notSubmitted: true};
			});
		  }
    }]);

