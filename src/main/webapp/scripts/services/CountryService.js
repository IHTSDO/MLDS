'use strict';

angular.module('MLDS')
.factory('CountryService', ['$http', '$log', '$q', function($http, $log, $q){
	
		var countriesListQ = 
			$http.get('/app/rest/countries')
				.then(function(d){return d.data;});
		var service = {};
		
		service.countries = [];
		service.countriesByIsoCode2 = {};
		service.ready = countriesListQ;
		
		countriesListQ.then(function(countries){
			// append to countries list
			Array.prototype.push.apply(service.countries,countries);
			
			// fill countriesByCode map
			service.countries.map(function(c){
				service.countriesByIsoCode2[c.isoCode2] = c;
			});
			
			$log.log('CountryService', service);
		});
		
		return service;
		
	}]);