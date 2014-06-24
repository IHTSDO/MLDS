'use strict';

angular.module('MLDS')
.factory('CommercialUsageService', ['$http', '$rootScope', '$log', '$q', 'Events', 
                                    function($http, $rootScope, $log, $q, Events){
		var lastId = 100;
		
		function fakeId() {
			lastId += 1;
			return lastId;
		}
		
		var fakeReports = [
			{
				commercialUsageId: fakeId(),
				startDate: new Date('2013-01-01'),
				endDate: new Date('2013-06-30'),
				submitted: new Date('2013-08-01'),
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
				startDate: new Date('2013-07-01'),
				endDate: new Date('2013-12-31'),
				submitted: new Date('2014-01-04'),
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

		function wrapData(data) {
			return {
				data: data
			};
		}
		
		var service = {};
		
		service.getUsageReports = function(licenseeId) {
			//return $http.get('/app/rest/licensees/{licenseeId}/commercialUsages');
			return $q.when(wrapData(fakeReports));
		};

		service.createUsageReport = function(licenseeId, startDate, endDate) {
			//return $http.post('/app/rest/licensees/{licenseeId}/commercialUsages');
			//FIXME does not duplicate last report
			var newReport = {
					commercialUsageId: fakeId(),
					//FIXME hack for 2nd Jan rather than 1st due to local/universal time...
					startDate: startDate,
					endDate: endDate,
					created: new Date(),
					usage: [
					]
			};
			fakeReports.push(newReport);
			return $q.when(wrapData(newReport));
		};

		service.getUsageReport = function(reportId) {
			//return $http.get('/app/rest/commercialUsages/{commercialUsageId}');
			return $q.when(wrapData(fakeFindUsageReport(parseInt(reportId, 10))));
		};

		service.addUsageEntry = function(usageReport, entry) {
			//return $http.post('app/rest/commercialUsages/{commercialUsageId}');
			entry.commercialUsageEntryId = fakeId();  
			usageReport.usage.push(entry);
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData(entry));
		};
		
		service.updateUsageEntry = function(usageReport, entry) {
			//return $http.put('/app/rest/commercialUsageEntries/{commercialUsageEntryId}');
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData(entry));
		};

		service.deleteUsageEntry = function(usageReport, entry) {
			//return $http.delete('/app+/rest/commercialUsageEntries/{commercialUsageEntryId}');
			fakeReports.forEach(function(usageReport) {
				for (var i = 0; i < usageReport.usage.length; i++) {
				    if (usageReport.usage[i].commercialUsageEntryId === entry.commercialUsageEntryId) {
				    	usageReport.usage.splice(i--, 1);
				    }
				}
			});
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData({}));
		};

		return service;
	}]);