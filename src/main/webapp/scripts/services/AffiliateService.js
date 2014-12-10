'use strict';

angular.module('MLDS')
.factory('AffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', '$resource', 'ApplicationUtilsService',
                                    function($http, $rootScope, $log, $q, Session, $resource, ApplicationUtilsService){

	var service = {};
	
	service.affiliatesResource = $resource('/app/rest/affiliates');

	service.allAffiliates = function(q) {
		return $http.get('/app/rest/affiliates?q='+encodeURIComponent(q));
	};
	
	service.filterAffiliates = function(q, page, pageSize, member) {
		return $http.get('/app/rest/affiliates?q='+encodeURIComponent(q)+'&$page='+encodeURIComponent(page)+'&$pageSize='+encodeURIComponent(pageSize)+(member?'&$filter='+encodeURIComponent('homeMember eq \''+member.key+'\''):''));
	};

	service.myAffiliate = function() {
		return $http.get('/app/rest/affiliates/me', {ignoreAuthModule:true})
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
	
	service.updateAffiliate = function(affiliate) {
		//FIXME workaround for unmodifiable collection issue..
		var affiliateCopy = angular.copy(affiliate);
		affiliateCopy.commercialUsages = [];
		affiliateCopy.applications = [];
		if (affiliateCopy.application) {
			affiliateCopy.application.commercialUsage = null;
		}
		//TODO optimize http method size by stripping out some of child elements
		return $http.put('/app/rest/affiliates/'+ encodeURIComponent(affiliate.affiliateId), affiliateCopy);
	};
	
	service.createLogin = function createLogin(affiliate) {
		return $http.post('/app/rest/account/create', affiliate);
	};
	
	service.updateAffiliateDetails = function(affiliateId, affiliateDetails) {
		var promise = $http.put('/app/rest/affiliates/'+encodeURIComponent(affiliateId)+'/detail', affiliateDetails);
		promise.then(function(result) {
			var affiliateDetails = result.data;
			if (affiliateDetails.email === Session.login) {
				Session.updateUserName(affiliateDetails.firstName, affiliateDetails.lastName);
			}
		});

		return promise;
	};

	service.myAffiliates = function() {
		return $http.get('/app/rest/affiliates/me');
	};

	service.affiliate = function(affiliateId) {
		return $http.get('/app/rest/affiliates/'+ encodeURIComponent(affiliateId));
	};
	
	service.affiliates = function(username) {
		return $http.get('/app/rest/affiliates/creator/'+encodeURIComponent(username));
	};

	service.affiliateIsCommercial = function(affiliate) {
		return affiliate.type === 'COMMERCIAL';
	};
	
	service.isApplicationApproved = function(affiliate) {
		return affiliate && ApplicationUtilsService.isApplicationApproved(affiliate.application);
	};

	return service;
}]);

