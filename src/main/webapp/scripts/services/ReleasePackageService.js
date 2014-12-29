'use strict';

angular.module('MLDS')
.factory('ReleasePackageService', ['$http', '$log', '$q', '$window', '$location', function($http, $log, $q, $window, $location){
	
	var service = {};

	service.updateReleaseLicense = function(releasePackageId, file) {
		var formData = new FormData();
        formData.append('file', file);
		
		return $http.post('/app/rest/releasePackages/' + encodeURIComponent(releasePackageId) + '/license', formData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        });
	};
	
	service.getReleaseLicense = function(releasePackageId) {
		$window.open('/app/rest/releasePackages/' + encodeURIComponent(releasePackageId) + '/license', '_blank');
	};
	
	return service;
	
}]);