'use strict';

angular.module('MLDS')
.factory('LicenseeService', ['$http', '$rootScope', '$log', '$q', 'Session', 
                                    function($http, $rootScope, $log, $q, Session){

	var service = {};
	
	service.myLicensees = function() {
		//FIXME chose which userid to send...
		return $http.get('/app/rest/licensees');
	};
	
	return service;
}]);

