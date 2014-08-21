'use strict';

mldsApp.factory('ApplicationUtilsService', ['ApprovalStateService', function(ApprovalStateService){
		
		var service = {};
			
		service.isApplicationWaitingForApplicant = function isApplicationWaitingForApplicant(application) {
			return ApprovalStateService.isWaitingForApplicant(application.approvalState);
		};

		service.isApplicationApproved = function isApplicationApproved(application) {
			return ApprovalStateService.isApproved(application.approvalState);
		};

		service.isApplicationRejected = function isApplicationRejected(application) {
			return ApprovalStateService.isRejected(application.approvalState);
		};

		service.isApplicationIncomplete = function isApplicationIncomplete(application) {
			return ApprovalStateService.isIncomplete(application.approvalState);
		};
			
		service.isApplicationPending = function isApplicationPending(application) {
			return ApprovalStateService.isPending(application.approvalState);
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