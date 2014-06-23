'use strict';

angular.module('MLDS')
.factory('CommercialUsageService', ['$http', '$rootScope', '$log', '$q', 'Events', function($http, $rootScope, $log, $q, Events){
		var lastId = 100;
		
		function fakeId() {
			lastId += 1;
			return lastId;
		}
		
		var fakeReports = [
			{
				commercialUsageId: fakeId(),
				key: '2013',
				created: new Date(),
				usage: [
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 1',
				        	startDate: new Date(),
				        	countryCode: 'CA'
				        }
				]
				
			}, {
				commercialUsageId: fakeId(),
				key: '2014',
				created: new Date(),
				usage: [
				        {
			        	commercialUsageEntryId: fakeId(),
			        	name: 'Hospital 1',
			        	startDate: new Date(),
			        	endDate: new Date(),
			        	countryCode: 'CA'
				        },
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 2',
				        	startDate: new Date(),
				        	endDate: new Date(),
				        	countryCode: 'CA'
				        },
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 3',
				        	startDate: new Date(),
				        	endDate: new Date(),
				        	countryCode: 'US'
					        }

				]
			}
		]; 
        
        
		function fakeFindUsageReport(reportId) {
			var found = fakeReports.filter(function(element) {
				return element.commercialUsageId === reportId;
			});
			return found ? found[0] : null;
		}

		var service = {};
		
		service.getUsageReports = function() {
			//return $http.get('/app/rest/licensees/{licenseeId}/commercialUsages');
			return $q.when(fakeReports);
		};

		service.createUsageReport = function() {
			//return $http.post('/app/rest/licensees/{licenseeId}/commercialUsages');
			//FIXME does not duplicate last report
			var newReport = {
					commercialUsageId: fakeId(),
					key: 'New',
					created: new Date(),
					usage: [
					]
			};
			fakeReports.push(newReport);
			return $q.when(newReport);
		};

		service.getUsageReport = function(reportId) {
			//return $http.get('/app/rest/commercialUsages/{commercialUsageId}');
			return $q.when(fakeFindUsageReport(reportId));
		};

		service.addUsageEntry = function(usageReport, entry) {
			//return $http.post('/app/rest/licensees/{licenseeId}/commercialUsages');
			entry.commercialUsageEntryId = fakeId();  
			usageReport.usage.push(entry);
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(entry);
		};
		
		service.updateUsageEntry = function(entry) {
			//return $http.put('/app/rest/commercialUsageEntries/{commercialUsageEntryId}');
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(entry);
		};

		service.deleteUsageEntry = function(entry) {
			//return $http.delete('/app+/rest/commercialUsageEntries/{commercialUsageEntryId}');
			fakeReports.forEach(function(usageReport) {
				for (var i = 0; i < usageReport.usage.length; i++) {
				    if (usageReport.usage[i].commercialUsageEntryId === entry.commercialUsageEntryId) {
				    	usageReport.usage.splice(i--, 1);
				    }
				}
			});
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when({});
		};

		return service;
	}]);