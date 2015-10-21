'use strict';

mldsApp.controller('PostAnnouncementController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'$timeout',
		'Session',
		'AnnouncementsService',
		'MemberService',
		function($scope, $log, $location, $modal, $timeout, Session, AnnouncementsService, MemberService) {
			
			$scope.alerts = [];
			$scope.announcement = {
					subject: '',
					body: '',
					member: Session.member || {member: 'NONE'},
					additionalEmails: []
			};
			$scope.emailListString = Session.email || ''; 
			$scope.members = Session.isAdmin() ? MemberService.members : $scope.announcement.member;

			$scope.form = {};
	    	$scope.form.attempted = false;
	    	
	    	$scope.postAnnouncement = function () {
	    		if ($scope.form.$invalid) {
	    			$scope.form.attempted = true;
	    			return;
	    		}

	    		$scope.submitting = true;
	    		$scope.alerts.splice(0, $scope.alerts.length);
	    		
	    		$scope.announcement.additionalEmails = $scope.emailListString.split(/[ ,;]+/);
	    		

	    		AnnouncementsService.post({}, $scope.announcement)
	    			.$promise.then(function(result) {
	    				$scope.submitting = false;
	    				$timeout(function(){
		    				$scope.alerts.push({type: 'success', msg: 'Announcement has been successfully posted.'});
	    				});
	    			})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
	        };
		} ]);
