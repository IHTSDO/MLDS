'use strict';

angular.module('MLDS')
.factory('LookupCollector', ['$q', 'Session', 'CountryService', function($q, Session, CountryService){
	return { promise: $q.all(Session.promise, CountryService.promise)};
}]);
