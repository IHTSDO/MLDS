'use strict';

mldsApp.factory('ApplicationUtilsService', [function(){
		
		var service = {};
			
		service.isApplicationWaitingForApplicant = function isApplicationWaitingForApplicant(application) {
			return (!application.approvalState
				|| application.approvalState === 'NOT_SUBMITTED'
				|| application.approvalState === 'CHANGE_REQUESTED');
		};

		service.isApplicationApproved = function isApplicationApproved(application) {
			return (application.approvalState === 'APPROVED');
		};

		service.isApplicationRejected = function isApplicationRejected(application) {
			return (application.approvalState === 'REJECTED');
		};

		service.isApplicationIncomplete = function isApplicationIncomplete(application) {
			return (application.approvalState !== 'APPROVED'
				&& application.approvalState !== 'REJECTED');
		};
			
		service.isApplicationPending = function isApplicationPending(application) {
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
		
		service.isPrimaryApplication = function isPrimaryApplication(application) {
			return (application.applicationType === 'PRIMARY');
		};

		service.isExtensionApplication = function isExtensionApplication(application) {
			return (application.applicationType === 'EXTENSION');
		};
		
		return service;
	}]);