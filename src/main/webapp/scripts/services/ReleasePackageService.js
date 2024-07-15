'use strict';

angular.module('MLDS')
.factory('ReleasePackageService', ['$http', '$log', '$q', '$window', '$location', function($http, $log, $q, $window, $location){

	let service = {};

	service.updateReleaseLicense = function(releasePackageId, file) {
		let formData = new FormData();
        formData.append('file', file);

		return $http.post('/api/releasePackages/' + encodeURIComponent(releasePackageId) + '/license', formData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        });
	};

	service.getReleaseLicense = function(releasePackageId) {
		$window.open('/api/releasePackages/' + encodeURIComponent(releasePackageId) + '/license', '_blank');
	};

	service.updateArchive = function(releaseVersionId, isArchive){
    	return $http.post('/api/updateArchive/' + encodeURIComponent(releaseVersionId), null, {
                params: { isArchive: isArchive }
        })
            .then(function(response) {
                return response.data;
        })
            .catch(function(error) {
                $log.error('Error updating archive:', error);
                throw error;
        });
    };

	return service;

}]);
