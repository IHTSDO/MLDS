'use strict';

mldsApp.factory('ApplicationUtilsService', ['ApprovalStateUtils', function(ApprovalStateUtils){
		
		var service = {};
			
		service.isApplicationWaitingForApplicant = function isApplicationWaitingForApplicant(application) {
			return ApprovalStateUtils.isWaitingForApplicant(application.approvalState);
		};

		service.isApplicationApproved = function isApplicationApproved(application) {
			return ApprovalStateUtils.isApproved(application.approvalState);
		};

		service.isApplicationRejected = function isApplicationRejected(application) {
			return ApprovalStateUtils.isRejected(application.approvalState);
		};

		service.isApplicationIncomplete = function isApplicationIncomplete(application) {
			return ApprovalStateUtils.isIncomplete(application.approvalState);
		};
			
		service.isApplicationPending = function isApplicationPending(application) {
			return ApprovalStateUtils.isPending(application.approvalState);
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