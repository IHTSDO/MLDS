'use strict';

angular.module('MLDS')
.factory('AffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', '$resource',
                                    function($http, $rootScope, $log, $q, Session, $resource){

	var service = {};
	
	service.affiliatesResource = $resource('/app/rest/affiliates');
	
	service.myAffiliates = function() {
		return $http.get('/app/rest/affiliates/me');
	};
	
	service.affiliates = function(username) {
		return $http.get('/app/rest/affiliates/creator/'+encodeURIComponent(username));
	};
	
	service.affiliateIsCommercial = function(affiliate) {
		return affiliate.type === 'COMMERCIAL';
	};

	
	return service;
}]);

