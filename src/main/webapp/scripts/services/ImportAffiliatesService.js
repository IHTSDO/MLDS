'use strict';

angular.module('MLDS')
.factory('ImportAffiliatesService', ['$http', '$log', 
                                    function($http, $log){

	var service = {};
	
	service.importAffiliates = function(affiliatesFile) {
		var formData = new FormData();
        formData.append('file', affiliatesFile);
        return $http.post('/api/affiliates/csv', formData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        });
	};

	service.exportAffiliatesUrl = '/api/affiliates/csv';
	
	service.importSpec = function() {
		return $http.get('/api/affiliates/csvSpec');
		//return $http.get('/api/affiliates/csv', {headers: {'Accept':'application/csv+spec'}});
	};
	
	return service;
}]);

