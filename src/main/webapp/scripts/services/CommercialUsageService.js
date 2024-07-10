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
			return $http.get('/api/affiliates/'+affiliateId+'/commercialUsages');
		};

		service.getSubmittedUsageReports = function(page, pageSize, orderby) {
             let params = {
                  page: page,
                  size: pageSize,
                  orderby: orderby,
                  $filter: "approvalState/not submitted eq false"
                  };
             return $http.get('/api/commercialUsages/', { params: params });
        };

		service.createUsageReport = function(affiliateId, startDate, endDate) {
			return $http.post('/api/affiliates/'+affiliateId+'/commercialUsages',
					{
						startDate: serializeDate(startDate),
						endDate: serializeDate(endDate)
					});
		};

		service.currentCommercialUsageReport = {};

		// Ensure that stateful service state is cleared on logout
		$rootScope.$on('event:auth-loginConfirmed', resetCurrerntCommercialUsageReport);
		$rootScope.$on('event:auth-loginCancelled', resetCurrerntCommercialUsageReport);

		function resetCurrerntCommercialUsageReport() {
			service.currentCommercialUsageReport = {};
		}

		service.getUsageReport = function(reportId) {
		   var usagePromise = $http.get('/api/commercialUsages/'+reportId);
		   usagePromise.then(function(response){
		           service.currentCommercialUsageReport = response.data;
		   });
		   return usagePromise;
		};

		service.updateUsageReportContext = function(usageReport, options) {
			var httpPromise = $http.put('/api/commercialUsages/'+usageReport.commercialUsageId+'/context', usageReport.context);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.updateUsageReportType = function(usageReport, options) {
			var httpPromise = $http.put('/api/commercialUsages/'+usageReport.commercialUsageId+'/type/'+encodeURIComponent(usageReport.type));
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.addUsageEntry = function(usageReport, entry, options) {
			var httpPromise = $http.post('/api/commercialUsages/'+usageReport.commercialUsageId, serializeCommercialEntry(entry));
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};


		service.updateUsageEntry = function(usageReport, entry, options) {
			var httpPromise = $http.put('/api/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId,
					serializeCommercialEntry(entry)
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};


		service.deleteUsageEntry = function(usageReport, entry, options) {
			var httpPromise = $http['delete']('/api/commercialUsages/'+usageReport.commercialUsageId+'/entries/'+entry.commercialUsageEntryId);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};


		service.addUsageCount = function(usageReport, count, options) {
			var httpPromise = $http.post('/api/commercialUsages/'+usageReport.commercialUsageId+'/countries', serializeCommercialCount(count));
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};


		service.updateUsageCount = function(usageReport, count, options) {
			var httpPromise = $http.put('/api/commercialUsages/'+usageReport.commercialUsageId+'/countries/'+count.commercialUsageCountId,
					serializeCommercialCount(count)
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};


		service.deleteUsageCount = function(usageReport, count, options) {
			var httpPromise = $http['delete']('/api/commercialUsages/'+usageReport.commercialUsageId+'/countries/'+count.commercialUsageCountId);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};


		service.submitUsageReport = function(usageReport, options) {
			var httpPromise = $http.post('/api/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'SUBMIT'
					}
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.retractUsageReport = function(usageReport, options) {
			var httpPromise = $http.post('/api/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: 'RETRACT'
					}
				);
			notifyUsageUpdatedIfRequired(httpPromise, options);
			return httpPromise;
		};

		service.updateUsageReport = function(usageReport, newState) {
			return $http.post('/api/commercialUsages/'+usageReport.commercialUsageId+'/approval',
					{
						transition: newState
					}
				);
		};

		function generateRangeEntry(start, end) {
			return {
				description: ''+start.format('YYYY-MM')+' - '+end.format('YYYY-MM'),
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
