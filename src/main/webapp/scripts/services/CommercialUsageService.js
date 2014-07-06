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
		};
		
		function serializeCommercialEntry(entry) {
			var serializable = angular.copy(entry);
			serializable.startDate = serializeDate(serializable.startDate);
			serializable.endDate = serializeDate(serializable.endDate);
			return serializable;
		};

		function serializeCommercialCount(count) {
			return count;
		};

		var service = {};
		
		
		service.getUsageReports = function(licenseeId) {
			return $http.get('/app/rest/licensees/'+licenseeId+'/commercialUsages');
		};

		
		service.createUsageReport = function(licenseeId, startDate, endDate) {
			return $http.post('/app/rest/licensees/'+licenseeId+'/commercialUsages',
					{
						startDate: serializeDate(startDate),
						endDate: serializeDate(endDate)
					});
		};

		
		service.getUsageReport = function(reportId) {
			return $http.get('/app/rest/commercialUsages/'+reportId);
		};

		service.updateUsageReportContext = function(usageReport, options) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/context', usageReport.context);
			httpPromise.then(function() {
				if (!options || !options.skipBroadcast) {
					$rootScope.$broadcast(Events.commercialUsageUpdated);
				}
			});
			return httpPromise;
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
			var httpPromise = $http['delete']('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		
		service.addUsageCount = function(usageReport, count) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/countries',
					serializeCommercialCount(count));
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};
		
		
		service.updateUsageCount = function(usageReport, count) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/countries/'+count.commercialUsageCountId,
					serializeCommercialCount(count)
				);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		
		service.deleteUsageCount = function(usageReport, count) {
			var httpPromise = $http['delete']('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/countries/'+count.commercialUsageCountId);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		
		service.submitUsageReport = function(usageReport) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'SUBMIT'
					}
				);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		service.retractUsageReport = function(usageReport) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'RETRACT'
					}
				);
			httpPromise.then(function() {
				$rootScope.$broadcast(Events.commercialUsageUpdated);	
			});
			return httpPromise;
		};

		function generateRangeEntry(start, end) {
			return {
				description: ''+start.format('MMM')+' - '+end.format('MMM YYYY'),
				startDate: start.toDate(),
				endDate: end.toDate()
			};
		};
		
		service.generateRanges = function generateRanges() {
			var ranges = [];
			var date = moment().local();
			var periods = 6;
			for (var i = 0; i < periods; i++) {
				var isFirstHalfOfYear = date.isBefore(date.clone().month(6).startOf('month'));
				if (isFirstHalfOfYear) {
					date = date.startOf('year');
					var end = date.clone().month(5).endOf('month');
					ranges.push(generateRangeEntry(date, end));
				} else {
					date = date.month(6).startOf('month');
					var end = date.clone().endOf('year');
					ranges.push(generateRangeEntry(date, end));
				}
				date = date.clone().subtract(2, 'months');
			}
			return ranges;
		};
		
		
		
		return service;
	}]);