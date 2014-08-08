'use strict';

angular.module('MLDS')
.factory('ImportAffiliatesService', ['$http', '$log', 
                                    function($http, $log){

	var service = {};
	
	service.importAffiliates = function(affiliatesFile) {
		var formData = new FormData();
        formData.append('file', affiliatesFile);
        return $http.post('/app/rest/affiliates/import', formData, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        });
	};

	return service;
}]);

