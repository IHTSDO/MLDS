'use strict';

mldsApp.factory('UserRegistrationService', ['$http', '$rootScope', '$log', 'Events', function($http, $rootScope, $log, Events){
	var service = {};
	
	service.getUsers = function() {
				return $http.get('/api/users');
			};
		
	service.createUser = function(user) {
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
			};
						
			service.getApplications = function() {
				return $http.get('/api/applications');
			};
			
			service.getApplicationsPending = function() {
				return $http.get('/api/applications?$filter='+encodeURIComponent('approvalState/pending eq true'));
			};

			service.filterPendingApplications = function(q, page, pageSize, member, orderBy, reverseSort) {
				return $http.get('/api/applications?'+
						/* q */
						'$filter='+encodeURIComponent('approvalState/pending eq true')+
						'&$page='+encodeURIComponent(page)+
						'&$pageSize='+encodeURIComponent(pageSize)+
						(member?'&$filter='+encodeURIComponent('homeMember eq \''+member.key+'\''):'')+
						(orderBy?'&$orderby='+encodeURIComponent(orderBy)+(reverseSort?' desc':''):'')
						);
			};

			service.getApplicationById = function(applicationId) {
				return $http.get('/api/applications/'+encodeURIComponent(applicationId));
			};

			service.getApplication = function() {
				return $http.get('/api/applications/me');
			};
			
			service.createExtensionApplication = function createExtensionApplication(member) {
				$log.log('createExtensionApplication');
				return $http.post('/api/applications', {memberKey: member.key, applicationType: 'EXTENSION'});
			};
			
			service.submitApplication = function submitApplication(applicationForm, applicationId) {
				$log.log('submitApplication', applicationForm);
				return $http.post('/api/applications/'+encodeURIComponent(applicationId)+'/registration', applicationForm);
			};
			
			service.saveApplication = function saveApplication(applicationForm, applicationId) {
				$log.log('saveApplication', applicationForm);
				return $http.put('/api/applications/'+encodeURIComponent(applicationId)+'/registration', applicationForm);
			};
			
			service.approveApplication = function approveApplication(application, approvalStatus) {
				$log.log('approveApplication', approvalStatus);
				return $http.post('/api/applications/'+encodeURIComponent(application.applicationId)+'/approve', approvalStatus);
			};
			
			service.updateApplicationNoteInternal = function(application) {
				return $http.put('/api/applications/'+encodeURIComponent(application.applicationId)+'/notesInternal', application.notesInternal);
			};
			
			// Let's move to using this one
			service.updateApplication = function (application) {
				return $http.post('/api/applications/'+encodeURIComponent(application.applicationId), application);
			};
			
			service.deleteApplication = function (applicationId) { 
				return $http['delete']('/api/applications/'+encodeURIComponent(applicationId));
			};
			
		return service;
		
	}]);