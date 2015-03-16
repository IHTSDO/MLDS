'use strict';

angular.module('MLDS')
.service('CountryService', ['$http', '$log', '$q', '$translate', function($http, $log, $q, $translate){
	
		var countriesListQ = 
			$http.get('/app/rest/countries')
				.then(function(d){return d.data;});
		var service = {};
		
		service.countries = [];
		service.countriesByIsoCode2 = {};
		service.countriesUsingMLDS = [];
		service.ready = countriesListQ;
		
		countriesListQ.then(function(countries){
			// append to countries list
			var tempCountriesUsingMLDS = [];
			_.each(countries,function(country) {
				country.commonName = $translate.instant('global.country.'+country.isoCode2);
				//If this country is managed by someone other than IHTSDO then we know they're using MLDS themselves.
				if (country.member.key != "IHTSDO") {
					tempCountriesUsingMLDS.push(country.isoCode2);
				}
			});
			Array.prototype.push.apply(service.countries,countries);
			Array.prototype.push.apply(service.countriesUsingMLDS,tempCountriesUsingMLDS);
			
			service.countries.sort(function(a, b) {
				var x = a.commonName.toLowerCase();
			    var y = b.commonName.toLowerCase();
			    return x < y ? -1 : x > y ? 1 : 0;
			});
			
			// fill countriesByCode map
			service.countries.map(function(c){
				service.countriesByIsoCode2[c.isoCode2] = c;
			});
			
			// $log.log('CountryService', service);
		});
		
		return service;
		
	}]);