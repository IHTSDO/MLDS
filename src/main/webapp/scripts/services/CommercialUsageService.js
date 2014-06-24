'use strict';

angular.module('MLDS')
.factory('CommercialUsageService', ['$http', '$rootScope', '$log', '$q', 'Events', '$filter',
                                    function($http, $rootScope, $log, $q, Events, $filter){
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
				type: entry.type,
				startDate: serializeDate(entry.startDate),
				endDate: serializeDate(entry.endDate),
				country: entry.country,
				created: entry.created
			};
		}
		
		var service = {};
		
		
		service.getUsageReports = function(licenseeId) {
			return $http.get('/app/rest/licensees/'+8/*licenseeId*/+'/commercialUsages');
		};

		
		service.createUsageReport = function(licenseeId, startDate, endDate) {
			return $http.post('/app/rest/licensees/'+8/*licenseeId*/+'/commercialUsages',
					{
						startDate: serializeDate(startDate),
						endDate: serializeDate(endDate)
					});
		};

		
		service.getUsageReport = function(reportId) {
			return $http.get('/app/rest/commercialUsages/'+reportId);
		};

		
		service.addUsageEntry = function(usageReport, entry) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId,
					serializeCommercialEntry(entry));
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
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

		
		service.deleteUsageEntry = function(usageReport, entry) {
			var httpPromise = $http.delete('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		
		return service;
	}]);