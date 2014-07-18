'use strict';

angular.module('MLDS')
.factory('LicenseeService', ['$http', '$rootScope', '$log', '$q', 'Session', '$resource',
                                    function($http, $rootScope, $log, $q, Session, $resource){

	var service = {};
	
	service.licenseesResource = $resource('/app/rest/licensees');
	
	service.myLicensees = function() {
		//FIXME chose which userid to send...
		return $http.get('/app/rest/licensees/me');
	};
	
	service.licensees = function(username) {
		return $http.get('/app/rest/licensees/creator/'+encodeURIComponent(username));
	};
	
	service.licenseeIsCommercial = function(licensee) {
		return licensee.type === 'COMMERCIAL';
	};

	
	return service;
}]);

