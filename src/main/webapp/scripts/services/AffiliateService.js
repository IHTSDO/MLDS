'use strict';

angular.module('MLDS')
.factory('AffiliateService', ['$http', '$rootScope', '$log', '$q', 'Session', '$resource',
                                    function($http, $rootScope, $log, $q, Session, $resource){

	var service = {};
	
	service.affiliatesResource = $resource('/app/rest/affiliates');

	//FIXME remove fake details once real details are returned...
	function insertFakeDetails(affiliate) {
    	affiliate.affiliateDetails = {
    			firstName: 'John',
    			lastName: 'Smith',
    			email: 'email@com',
    			alternateEmail: 'alternative@com',
    			thirdEmail: 'third@com',
    			address: {
        			street: 'street',
        			city: 'city',
        			post: 'post',
        			country: {
        				isoCode2: 'CA',
        				commonName: 'Canada'
        			}
    			},
    			organizationName: 'Organization Name',
    			billingAddress: {
        			street: 'b street',
        			city: 'b city',
        			post: 'b post',
        			country: {
        				isoCode2: 'US',
        				commonName: 'United States'
        			}
    			},
    			landlineNumber: '+1 4156 762 0032',
    			landlineExtension: '123',
    			mobileNumber: '+1 416 999 99999'
    			
    	};
    }

	service.myAffiliate = function() {
		return $http.get('/app/rest/affiliates/me')
			.then(function(result) {
    			var affiliates = result.data;
    			if (affiliates && affiliates.length > 0) {
    				var affiliate = affiliates[0];
    				//insertFakeDetails(affiliate);
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

