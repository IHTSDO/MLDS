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
				usage: [
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 1',
				        	startDate: new Date(),
				        	countryCode: 'CA'
				        }
				]
				
			}, {
				commercialUsageId: 21/*FIXMEfakeId()*/,
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
			        	countryCode: 'CA',
			        	created: new Date()
				        },
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 2',
				        	startDate: new Date(),
				        	endDate: new Date(),
				        	countryCode: 'CA',
				        	created: new Date()
				        },
				        {
				        	commercialUsageEntryId: fakeId(),
				        	name: 'Hospital 3',
				        	startDate: new Date(),
				        	endDate: new Date(),
				        	countryCode: 'US',
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
				country: {
					isoCode2: entry.countryCode
				},
				created: entry.created
			}
		}
		
		var service = {};
		
		service.getUsageReports = function(licenseeId) {
			$http.get('/app/rest/licensees/'+8/*licenseeId*/+'/commercialUsages')
				.then(function(result) {
					$log.log('getUsageReports');
					$log.log(result);
				})
				.catch(function(message) {
					$log.log('getUsageReports FAILED')
					$log.log(message);
				});
			//FIXME FAKE
			return $q.when(wrapData(fakeReports));
		};

		service.createUsageReport = function(licenseeId, startDate, endDate) {
			$http.post('/app/rest/licensees/'+8/*licenseeId*/+'/commercialUsages',
					{
						startDate: serializeDate(startDate),
						endDate: serializeDate(endDate)
					})
				.then(function(result) {
					$log.log('createUsageReport');
					$log.log(result);
				})
				.catch(function(message) {
					$log.log('createUsageReport FAILED')
					$log.log(message);
				});
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
			$http.get('/app/rest/commercialUsages/'+reportId)
				.then(function(result) {
					$log.log('getUsageReport');
					$log.log(result);
				})
				.catch(function(message) {
					$log.log('getUsageReport FAILED')
					$log.log(message);
				});
			//FIXME FAKE
			return $q.when(wrapData(fakeFindUsageReport(parseInt(reportId, 10))));
		};

		service.addUsageEntry = function(usageReport, entry) {
			$http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId,
					serializeCommercialEntry(entry))
				.then(function(result) {
					$log.log('addUsageEntry '+result.data.commercialUsageEntryId);
					$log.log(result);
					entry.commercialUsageEntryId = result.data.commercialUsageEntryId;
				})
				.catch(function(message) {
					$log.log('addUsageEntry FAILED')
					$log.log(message);
				});
			//FIXME FAKE
			entry.commercialUsageEntryId = fakeId();  
			usageReport.usage.push(entry);
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData(entry));
		};
		
		service.updateUsageEntry = function(usageReport, entry) {
			$http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId,
					serializeCommercialEntry(entry))
				.then(function(result) {
					$log.log('updateUsageEntry');
					$log.log(result);
				})
				.catch(function(message) {
					$log.log('updateUsageEntry FAILED')
					$log.log(message);
				});
			//FIXME FAKE
			$rootScope.$broadcast(Events.commercialUsageUpdated);
			return $q.when(wrapData(entry));
		};

		service.deleteUsageEntry = function(usageReport, entry) {
			$http.delete('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId)
				.then(function(result) {
					$log.log('deleteUsageEntry');
					$log.log(result);
				})
				.catch(function(message) {
					$log.log('deleteUsageEntry FAILED')
					$log.log(message);
				});
			//FIXME FAKE
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