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
			
			getApplication: function() {
				return $http.get('/api/application');
			},
			
			submitApplication: function submitApplication(applicationForm) {
				$log.log('submitApplication', applicationForm);
				return $http.post('/api/application/submit', applicationForm);
			},
			
			saveApplication: function saveApplication(applicationForm) {
				$log.log('saveApplication', applicationForm);
				return $http.post('/api/application/save', applicationForm);
			},
			
			approveApplication: function approveApplication(application, approvalStatus) {
				$log.log('approveApplication', approvalStatus);
				return $http.post('/api/application/'+encodeURIComponent(application.applicationId)+'/approve', approvalStatus);
			},
			
			updateApplicationNoteInternal: function(application) {
				return $http.put('/api/application/'+encodeURIComponent(application.applicationId)+'/notesInternal', application.notesInternal);
			},
			
			isApplicationWaitingForApplicant: function isApplicationWaitingForApplicant(application) {
				return (!application.approvalState
					|| application.approvalState === 'NOT_SUBMITTED'
					|| application.approvalState === 'CHANGE_REQUESTED');
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