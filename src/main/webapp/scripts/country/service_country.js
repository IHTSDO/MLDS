'use strict';

mldsApp.factory('Countries', ['$resource', '$http',
    function ($resource, $http) {
        return $resource('api/countries/:isoCode2', {}, {
            'query': { 
            	method: 'GET', 
            	isArray: true, 
            	transformResponse: 
            		$http.defaults.transformResponse
            			.concat(function(data){
            			data = _.sortBy(data, function(o){return o.commonName;});
            			return data;})
            		},
            'get': { method: 'GET'}
        });
    }]);
