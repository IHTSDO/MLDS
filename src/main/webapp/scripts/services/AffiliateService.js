'use strict';

angular.module('MLDS')
.factory('AffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', '$resource', 'ApplicationUtilsService',
                                    function($http, $rootScope, $log, $q, Session, $resource, ApplicationUtilsService){

	var service = {};

//	service.affiliatesResource = $resource('/api/affiliates');

/*MLDS-996 - Front End Bug*/
  service.affiliatesResource = $resource('/api/affiliates', {}, {
         query: {
            isArray: false
        }
  });
/*MLDS-996 - Front End Bug*/
	service.allAffiliates = function(q) {
		return $http.get('/api/affiliates?q='+encodeURIComponent(q));
	};

	service.filterAffiliates = function(q, page, pageSize, member, standingState, standingStateNot, orderBy, reverseSort) {
		return $http.get('/api/affiliates?q='+encodeURIComponent(q)+
				'&$page='+encodeURIComponent(page)+
				'&$pageSize='+encodeURIComponent(pageSize)+
				(member?'&$filter='+encodeURIComponent('homeMember eq \''+member.key+'\''):'')+
				(orderBy?'&$orderby='+encodeURIComponent(orderBy)+(reverseSort?' desc':''):'')+
				(standingState?'&$filter='+encodeURIComponent((standingStateNot?'not ':'')+'standingState eq \''+standingState+'\''):'')
				);
	};

	service.myAffiliate = function() {
		return $http.get('/api/affiliates/me', {ignoreAuthModule:true})
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
		return $http.put('/api/affiliates/'+ encodeURIComponent(affiliate.affiliateId), affiliateCopy);
	};

	service.deleteAffiliate = function(affiliate) {
		return $http['delete']('/api/affiliates/'+ encodeURIComponent(affiliate.affiliateId));
	};

	service.createLogin = function createLogin(affiliate) {
		return $http.post('/api/account/create', affiliate);
	};

	service.updateAffiliateDetails = function(affiliateId, affiliateDetails) {
		var promise = $http.put('/api/affiliates/'+encodeURIComponent(affiliateId)+'/detail', affiliateDetails);
		promise.then(function(result) {
			var affiliateDetails = result.data;
			if (affiliateDetails.email === Session.login) {
				Session.updateUserName(affiliateDetails.firstName, affiliateDetails.lastName);
			}
		});

		return promise;
	};

	service.myAffiliates = function() {
		return $http.get('/api/affiliates/me');
	};

	service.affiliate = function(affiliateId) {
		return $http.get('/api/affiliates/'+ encodeURIComponent(affiliateId));
	};

	service.affiliates = function(username) {
		return $http.get('/api/affiliates/creator/'+encodeURIComponent(username));
	};

	service.affiliateIsCommercial = function(affiliate) {
		return affiliate.type === 'COMMERCIAL';
	};

	service.isApplicationApproved = function(affiliate) {
		return affiliate && ApplicationUtilsService.isApplicationApproved(affiliate.application);
	};

	return service;
}]);

