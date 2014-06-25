'use strict';

angular.module('MLDS')
.factory('CommercialUsageService', ['$http', '$rootScope', '$log', '$q', 'Events', 
                                    function($http, $rootScope, $log, $q, Events){
		function serializeDate(date) {
			if (date) {
				return moment(date).format('YYYY-MM-DD');
			} else {
				return null;
			}
		}
		
		function serializeCommercialEntry(entry) {
			var serializable = angular.copy(entry);
			serializable.startDate = serializeDate(serializable.startDate);
			serializable.endDate = serializeDate(serializable.endDate);
			return serializable;
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

		service.submitUsageReport = function(usageReport) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/submit'
				);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		
		return service;
	}]);