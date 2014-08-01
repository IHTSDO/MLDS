'use strict';

mldsApp.factory('UserRegistrationService', ['$http', '$rootScope', '$log', 'Events', function($http, $rootScope, $log, Events){
		return {
			getUsers: function() {
				return $http.get('/app/rest/users');
			},
		
			createUser : function(user) {
				var httpPromise = $http({
					    method: 'POST',
					    url: '/registrations/create',
					    params: user
					})
					.error(function(data) {
						$log.log(data);
						$rootScope.$broadcast(Events.registrationError, data);
					});
				
				httpPromise.then(function() {
					$rootScope.$broadcast(Events.newUser);
				});
				
				return httpPromise;
			},
						
			getApplications: function() {
				return $http.get('/api/applications');
			},
			
			getApplicationsPending: function() {
				return $http.get('/app/rest/applications?$filter='+encodeURIComponent('approvalState/pending eq true'));
			},

			getApplicationById: function(applicationId) {
				return $http.get('/app/rest/applications/'+encodeURIComponent(applicationId));
			},

			getApplication: function() {
				return $http.get('/app/rest/applications/me');
			},
			
			createExtensionApplication: function createExtensionApplication() {
				$log.log('createExtensionApplication');
				return $http.post('/app/rest/applications', {applicationType: 'EXTENSION'});
			},
			
			submitApplication: function submitApplication(applicationForm, applicationId) {
				$log.log('submitApplication', applicationForm);
				return $http.post('/app/rest/applications/'+encodeURIComponent(applicationId)+'/registration', applicationForm);
			},
			
			saveApplication: function saveApplication(applicationForm, applicationId) {
				$log.log('saveApplication', applicationForm);
				return $http.put('/app/rest/applications/'+encodeURIComponent(applicationId)+'/registration', applicationForm);
			},
			
			approveApplication: function approveApplication(application, approvalStatus) {
				$log.log('approveApplication', approvalStatus);
				return $http.post('/app/rest/applications/'+encodeURIComponent(application.applicationId)+'/approve', approvalStatus);
			},
			
			updateApplicationNoteInternal: function(application) {
				return $http.put('/app/rest/applications/'+encodeURIComponent(application.applicationId)+'/notesInternal', application.notesInternal);
			},
			
			isApplicationWaitingForApplicant: function isApplicationWaitingForApplicant(application) {
				return (!application.approvalState
					|| application.approvalState === 'NOT_SUBMITTED'
					|| application.approvalState === 'CHANGE_REQUESTED');
			},

			isApplicationApproved: function isApplicationApproved(application) {
				return (application.approvalState === 'APPROVED');
			},

			isApplicationRejected: function isApplicationRejected(application) {
				return (application.approvalState === 'REJECTED');
			},

			isApplicationIncomplete: function isApplicationIncomplete(application) {
				return (application.approvalState !== 'APPROVED'
					&& application.approvalState !== 'REJECTED');
			},

			
			isApplicationPending: function isApplicationPending(application) {
				return (application.approvalState === 'SUBMITTED'
					|| application.approvalState === 'RESUBMITTED'
					|| application.approvalState === 'REVIEW_REQUESTED');
			},


			getOrganizationTypes: function getOrganizationTypes() {
				return ['PUBLIC_HEALTH_ORGANIZATION', 'PRIVATE_HEALTH_ORGANIZATION', 
				        'RESEARCH_AND_DEVELOPMENT_ORGANIZATION', 'HEALTHERCARE_APPLICATION_DEVELOPER',
				        'GENERAL_PRACTITIONER_PRACTICE', 'EDUCATIONAL_INSTITUTE', 'OTHER'
				];
			}
		};
		
	}]);