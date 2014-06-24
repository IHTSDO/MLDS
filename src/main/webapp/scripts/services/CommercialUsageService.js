'use strict';

angular.module('MLDS')
.factory('CommercialUsageService', ['$http', '$rootScope', '$log', '$q', 'Events', '$filter',
                                    function($http, $rootScope, $log, $q, Events, $filter){
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
				entries: [
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 1',
				        	startDate: new Date(),
				        	country: {
				        		isoCode2: 'CA'
				        	}
				        }
				]
				
			}, {
				commercialUsageId: 21/*FIXMEfakeId()*/,
				startDate: new Date('2013-07-01'),
				endDate: new Date('2013-12-31'),
				submitted: new Date('2014-01-04'),
				created: new Date(),
				entries: [
				        {
			        	commercialUsageEntryId: fakeId(),
			        	name: 'Hospital 1',
			        	startDate: new Date(),
			        	endDate: new Date(),
			        	country: {
			        		isoCode2: 'CA'
			        	},
			        	created: new Date()
				        },
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 2',
				        	startDate: new Date(),
				        	endDate: new Date(),
				        	country: {
				        		isoCode2: 'CA'
				        	},
				        	created: new Date()
				        },
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 3',
				        	startDate: new Date(),
				        	endDate: new Date(),
				        	country: {
				        		isoCode2: 'US'
				        	},
				        	created: new Date()
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
		
		function serializeDate(date) {
			if (date) {
				return $filter('date')(date, 'yyyy-MM-dd');
			} else {
				return null;
			}
		}
		
		function serializeCommercialEntry(entry) {
			return {
				commercialUsageEntryId: entry.commercialUsageEntryId,
				name: entry.name,
				startDate: serializeDate(entry.startDate),
				endDate: serializeDate(entry.endDate),
				country: entry.country,
				created: entry.created
			}
		}
		
		var service = {};
		
		var fakeService = {};
		
		service.getUsageReports = function(licenseeId) {
			return $http.get('/app/rest/licensees/'+8/*licenseeId*/+'/commercialUsages');
		};
		fakeService.getUsageReports = function(licenseeId) {
			//FIXME FAKE
			return $q.when(wrapData(fakeReports));
		};

		
		service.createUsageReport = function(licenseeId, startDate, endDate) {
			return $http.post('/app/rest/licensees/'+8/*licenseeId*/+'/commercialUsages',
					{
						startDate: serializeDate(startDate),
						endDate: serializeDate(endDate)
					});
		};
		fakeService.createUsageReport = function(licenseeId, startDate, endDate) {
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
			return $http.get('/app/rest/commercialUsages/'+reportId);
		};
		fakeService.getUsageReport = function(reportId) {
			//FIXME FAKE
			return $q.when(wrapData(fakeFindUsageReport(parseInt(reportId, 10))));
		};

		
		service.addUsageEntry = function(usageReport, entry) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId,
					serializeCommercialEntry(entry));
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};
		fakeService.addUsageEntry = function(usageReport, entry) {
			entry.commercialUsageEntryId = fakeId();  
			usageReport.entries.push(entry);
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData(entry));
		};
		
		
		service.updateUsageEntry = function(usageReport, entry) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId,
					serializeCommercialEntry(entry)
				);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};
		fakeService.updateUsageEntry = function(usageReport, entry) {
			//FIXME FAKE
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData(entry));
		};

		
		service.deleteUsageEntry = function(usageReport, entry) {
			var httpPromise = $http.delete('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};
		fakeService.deleteUsageEntry = function(usageReport, entry) {
			//FIXME FAKE
			fakeReports.forEach(function(usageReport) {
				for (var i = 0; i < usageReport.entries.length; i++) {
				    if (usageReport.entries[i].commercialUsageEntryId === entry.commercialUsageEntryId) {
				    	usageReport.entries.splice(i--, 1);
				    }
				}
			});
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData({}));
		};

		
		return service;
	}]);