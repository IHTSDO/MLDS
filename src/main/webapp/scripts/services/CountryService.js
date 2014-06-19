'use strict';

mldsApp.factory('CountryService', ['$http', '$log', '$q', function($http, $log, $q){
		return {
			getCountries: function() {
				return $q.when([
					{
						isoCode2: 'DK',
						isoCode3: 'DNK',
						commonName: 'Denmark'
					},
					{
						isoCode2: 'FR',
						isoCode3: 'FRA',
						commonName: 'France'
					},
					{
						isoCode2: 'UA',
						isoCode3: 'URE',
						commonName: 'United Arab Emirates'
					},
					{
						isoCode2: 'GB',
						isoCode3: 'GBP',
						commonName: 'United Kingdom'
					},
					{
						isoCode2: 'US',
						isoCode3: 'USA',
						commonName: 'United States'
					}
				]);
				//FIXME retrieve countries from server
				//return $http.get('/app/countries');
			},
		};
		
	}]);