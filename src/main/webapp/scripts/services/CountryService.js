'use strict';

mldsApp.factory('CountryService', ['$http', '$log', '$q', function($http, $log, $q){
	
		var countriesListQ = $http.get('/app/rest/countries')
			.then(function(d){return d.data;});
		var service = {};
		
		service.countries = [];
		service.countriesByCode = {};
		
		service.getCountries = function getCountries() {
			return countriesListQ;
		};
		
		countriesListQ.then(function(countries){
			// append to countries list
			Array.prototype.push.apply(service.countries,countries);
			
			// fill countriesByCode map
			service.countries.map(function(c){
				service.countriesByCode[c.isoCode2] = c;
			});
			
			$log.log('CountryService', service);
		});
		
		return service;
		
	}]);