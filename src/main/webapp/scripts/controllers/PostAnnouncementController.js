'use strict';

mldsApp.controller('PostAnnouncementController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'$timeout',
		'$route',
		'Session',
		'AnnouncementsService',
		'MemberService',
		function($scope, $log, $location, $modal, $timeout, $route, Session, AnnouncementsService, MemberService) {
			
			$scope.alerts = [];
			$scope.announcement = {
					subject: '',
					body: '',
					member: sessionMemberPopulated(),
					allAffiliates: false,
					additionalEmails: []
			};
			$scope.emailListString = Session.email || '';
			
			
			$scope.includeAllAffiliates = 0;
			$scope.isAdmin = Session.isAdmin();

			$scope.form = {};
	    	$scope.form.attempted = false;
	    	
	    	$scope.submitting = false
	    	$scope.completed = false;
	    	
	    	
	    	function sessionMemberPopulated() {
	    		var member = Session.member || {key: 'NONE'};
	    		var populatedMember = MemberService.membersByKey[member.key] || member;
	    		return populatedMember;
	    	}
	    	
	    	$scope.postAnnouncement = function () {
	    		$scope.form.attempted = true;
	    		
	    		if ($scope.form.$invalid) {
	    			return;
	    		}
	    		
	    		if ($scope.completed) {
	    			return;
	    		}

	    		$scope.submitting = true;
	    		$scope.alerts.splice(0, $scope.alerts.length);
	    		
	    		$scope.announcement.additionalEmails = $scope.emailListString.split(/[ ,;]+/);
	    		
	    		$scope.announcement.allAffiliates = (Session.isAdmin && $scope.includeAllAffiliates) ? true : false;

	    		AnnouncementsService.post({}, $scope.announcement)
	    			.$promise.then(function(result) {
	    				$scope.submitting = false;
	    				$scope.completed = true;
	    				$timeout(function(){
		    				$scope.alerts.push({type: 'success', msg: 'Announcement has been successfully posted.'});
	    				});
	    			})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
	        };
	        
	        $scope.newAnnouncement = function newAnnouncement() {
	        	$route.reload();
	        };
		} ]);
