'use strict';

mldsApp.factory('ApplicationUtilsService', [function(){
		
		var service = {};
			
		service.isApplicationWaitingForApplicant = function(application) {
			return (!application.approvalState
				|| application.approvalState === 'NOT_SUBMITTED'
				|| application.approvalState === 'CHANGE_REQUESTED');
		};

		service.isApplicationApproved = function(application) {
			return (application.approvalState === 'APPROVED');
		};

		service.isApplicationRejected = function(application) {
			return (application.approvalState === 'REJECTED');
		};

		service.isApplicationIncomplete = function(application) {
			return (application.approvalState !== 'APPROVED'
				&& application.approvalState !== 'REJECTED');
		};
			
		service.isApplicationPending = function(application) {
			return (application.approvalState === 'SUBMITTED'
				|| application.approvalState === 'RESUBMITTED'
				|| application.approvalState === 'REVIEW_REQUESTED');
		};

		service.getOrganizationTypes = function() {
			return ['PUBLIC_HEALTH_ORGANIZATION', 'PRIVATE_HEALTH_ORGANIZATION', 
			        'RESEARCH_AND_DEVELOPMENT_ORGANIZATION', 'HEALTHERCARE_APPLICATION_DEVELOPER',
			        'GENERAL_PRACTITIONER_PRACTICE', 'EDUCATIONAL_INSTITUTE', 'OTHER'
			];
		};
		
		return service;
	}]);