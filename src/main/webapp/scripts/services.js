'use strict';

/* Services */

mldsApp.factory('Activate', ['$resource',
    function ($resource) {
        return $resource('app/rest/activate', {}, {
            'get': { method: 'GET', params: {}, isArray: false}
        });
    }]);

mldsApp.factory('Account', ['$resource',
    function ($resource) {
        return $resource('app/rest/account', {}, {
        });
    }]);

mldsApp.factory('Password', ['$resource',
    function ($resource) {
        return $resource('app/rest/account/change_password', {}, {
        });
    }]);

mldsApp.factory('Sessions', ['$resource',
    function ($resource) {
        return $resource('app/rest/account/sessions/:series', {}, {
            'get': { method: 'GET', isArray: true}
        });
    }]);

mldsApp.factory('MetricsService', ['$resource',
    function ($resource) {
        return $resource('metrics/metrics', {}, {
            'get': { method: 'GET'}
        });
    }]);

mldsApp.factory('ThreadDumpService', ['$http',
    function ($http) {
        return {
            dump: function() {
                var promise = $http.get('dump').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

mldsApp.factory('HealthCheckService', ['$rootScope', '$http',
    function ($rootScope, $http) {
        return {
            check: function() {
                var promise = $http.get('health').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

mldsApp.factory('LogsService', ['$resource',
    function ($resource) {
        return $resource('app/rest/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel':  { method: 'PUT'}
        });
    }]);

mldsApp.factory('AuditsService', ['$http',
    function ($http) {
        return {
            findAll: function() {
                var promise = $http.get('/app/rest/audits').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findByDates: function(fromDate, toDate) {
                var promise = $http.get('/app/rest/audits?$filter='+encodeURIComponent('auditEventDate ge \''+fromDate+'\' and auditEventDate le \''+toDate+'\''))
               		.then(function (response) {
               			return response.data;
                });
                return promise;
            },
            findByAuditEventType: function(auditEventType) {
                var promise = $http.get('/app/rest/audits?$filter='+encodeURIComponent('auditEventType eq \''+auditEventType+'\''))
                	.then(function (response) {
                		return response.data;
                });
                return promise;
            },
            findByAffiliateId: function(affiliateId) {
                var promise = $http.get('/app/rest/audits?$filter='+encodeURIComponent('affiliateId eq \''+affiliateId+'\''))
            	.then(function (response) {
            		return response.data;
            });
            return promise;
            }
        };
    }]);

