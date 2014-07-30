'use strict';

angular.module('MLDS')
.factory('LookupCollector', ['$q', 'Session', 'CountryService', function($q, Session, CountryService){
	return $q.all(Session.promise, CountryService.promise);
}]);
