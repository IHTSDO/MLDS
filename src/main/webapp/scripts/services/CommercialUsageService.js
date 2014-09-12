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
			$log.log('count', count);
			
			return count;
		};

		var service = {};
		
		function broadcastCommercialUsageUpdate() {
			$rootScope.$broadcast(Events.commercialUsageUpdated);
		}

		function notifyUsageUpdatedIfRequired(httpPromise, options) {
			httpPromise.then(function() {
				if (!options || !options.skipBroadcast) {
					broadcastCommercialUsageUpdate();
				}
			});
		}

		service.broadcastCommercialUsageUpdate = function() {
			broadcastCommercialUsageUpdate();
		};

		service.getUsageReports = function(affiliateId) {
			return $http.get('/app/rest/affiliates/'+affiliateId+'/commercialUsages');
		};

		service.getSubmittedUsageReports = function() {			
			return $http.get('/app/rest/commercialUsages/?$filter='+encodeURIComponent('approvalState/submitted eq true'));
		};
		
		service.createUsageReport = function(affiliateId, startDate, endDate) {
			return $http.post('/app/rest/affiliates/'+affiliateId+'/commercialUsages',
					{
						startDate: serializeDate(startDate),
						endDate: serializeDate(endDate)
					});
		};

		service.currentCommercialUsageReport = {};
		
		service.getUsageReport = function(reportId) {
		   var usagePromise = $http.get('/app/rest/commercialUsages/'+reportId);
		   usagePromise.then(function(response){
		           service.currentCommercialUsageReport = response.data;
		   });
		   return usagePromise;
		};

		service.updateUsageReportContext = function(usageReport, options) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/context', usageReport.context);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.updateUsageReportType = function(usageReport, options) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/type/'+encodeURIComponent(usageReport.type));
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.addUsageEntry = function(usageReport, entry, options) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId, serializeCommercialEntry(entry));
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};
		
		
		service.updateUsageEntry = function(usageReport, entry, options) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId,
					serializeCommercialEntry(entry)
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		
		service.deleteUsageEntry = function(usageReport, entry, options) {
			var httpPromise = $http['delete']('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		
		service.addUsageCount = function(usageReport, count, options) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/countries', serializeCommercialCount(count));
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};
		
		
		service.updateUsageCount = function(usageReport, count, options) {
			var httpPromise = $http.put('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/countries/'+count.commercialUsageCountId,
					serializeCommercialCount(count)
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		
		service.deleteUsageCount = function(usageReport, count, options) {
			var httpPromise = $http['delete']('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/countries/'+count.commercialUsageCountId);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		
		service.submitUsageReport = function(usageReport, options) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'SUBMIT'
					}
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};
		
		service.retractUsageReport = function(usageReport, options) {
			var httpPromise = $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'RETRACT'
					}
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.reviewUsageReport = function(usageReport) {
			return $http.post('/app/rest/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'REVIEWED'
					}
				);
		};
		
		function generateRangeEntry(start, end) {
			return {
				description: ''+start.format('MMM')+' - '+end.format('MMM YYYY'),
				startDate: start.toDate(),
				endDate: end.toDate()
			};
		};
		
		
		function biannaulPeriods(periods) {
			var ranges = [];
			var date = moment().local();
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
		
		function annualPeriods(periods) {
			var ranges = [];
			var date = moment().local();
			
			for (var i = 0; i < periods; i++) {
				date = date.startOf('year');
				var end = date.clone().endOf('year');
				ranges.push(generateRangeEntry(date, end));
				
				date = date.clone().subtract(2, 'months');
			}
			
			return ranges;
		};
		
		service.generateRanges = function generateRanges() {
			var periods = 6;
			
			//return biannaulPeriods(periods);
			
			return annualPeriods(periods);
		};
		
		
		
		return service;
	}]);