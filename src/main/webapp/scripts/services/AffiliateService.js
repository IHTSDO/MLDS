'use strict';

angular.module('MLDS')
.factory('AffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', '$resource',
                                    function($http, $rootScope, $log, $q, Session, $resource){

	var service = {};
	
	service.affiliatesResource = $resource('/app/rest/affiliates');

	service.myAffiliate = function() {
		return $http.get('/app/rest/affiliates/me')
			.then(function(result) {
    			var affiliates = result.data;
    			if (affiliates && affiliates.length > 0) {
    				//FIXME extract first affiliate
    				var affiliate = affiliates[0];
    				result.data = affiliate;
    				return result;
    			} else {
    				result.data = null;
    				return result; 
    			}
			});
	};
	
	service.updateAffiliateDetails = function(affiliateId, affiliateDetails) {
		return $http.put('/app/rest/affiliates/'+encodeURIComponent(affiliateId)+'/detail', affiliateDetails);
	};

	service.myAffiliates = function() {
		return $http.get('/app/rest/affiliates/me');
	};

	service.affiliates = function(username) {
		return $http.get('/app/rest/affiliates/creator/'+encodeURIComponent(username));
	};

	service.affiliateIsCommercial = function(affiliate) {
		return affiliate.type === 'COMMERCIAL';
	};
	
	service.isApplicationApproved = function(affiliate) {
		return affiliate && affiliate.application && affiliate.application.approvalState === 'APPROVED';
	};

	
	return service;
}]);

