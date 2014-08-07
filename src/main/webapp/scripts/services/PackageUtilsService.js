'use strict';

angular.module('MLDS').factory('PackageUtilsService',
		[ '$resource', '$q', '$log', '$location', '$modal', 'Session', 'UserRegistrationService', 'UserAffiliateService', 'MemberService', 
		  function($resource, $q, $log, $location, $modal, Session, UserRegistrationService, UserAffiliateService, MemberService) {
			var service = {};
			
			service.isPackagePublished = function isPackagePublished(packageEntity) {
	        	for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
	        		if (packageEntity.releaseVersions[i].online) {
	        			return true;
	        		};
	        	}
	        	return false;
	        };
	        
	        service.getLatestPublishedDate = function getLatestPublishedDate(packageEntity) { 
	    		var latestPublishDate = null;
	    		for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
	    			if (latestPublishDate == null) {
	    				latestPublishDate = packageEntity.releaseVersions[i].publishedAt;
	    			} else if (new Date(packageEntity.releaseVersions[i].publishedAt) > new Date(latestPublishDate) ) {
	    				latestPublishDate = packageEntity.releaseVersions[i].publishedAt;
	    			};
	    		};
	    		latestPublishDate = latestPublishDate || new Date();
	    		return latestPublishDate;
	        };
			
	        service.isVersionOnline = function isVersionOnline(releaseVersion) {
	        	return releaseVersion.online ? true : false;
	        };
	        
	        service.isLatestVersion = function isLatestVersion(version, versions) {
	        	if (!version.publishedAt) {
	        		return false;
	        	}
	        	var versionPublishedDate = new Date(version.publishedAt);
	        	for(var i = 0; i < versions.length; i++) {
	        		if (versions[i].publishedAt && 
	    				(versionPublishedDate < new Date(versions[i].publishedAt) )) {
	    				return false;
	    			};
	        	};
	        	return true;
	        };
			
	        
	        service.updateVersionsLists = function updateVersionsLists(packgeEntity) {
	    		var results = { online: [], offline: [] };
	    		var publishedOfflineVersions = [];
	    		var nonPublishedOfflineVersions = [];
	    		
	    		for(var i = 0; i < packgeEntity.releaseVersions.length; i++) {
	    			if (packgeEntity.releaseVersions[i].online) {
	    				results.online.push(packgeEntity.releaseVersions[i]);
	    			} else {
	    				if (packgeEntity.releaseVersions[i].publishedAt) {
	    					publishedOfflineVersions.push(packgeEntity.releaseVersions[i]);
	    				} else {
	    					nonPublishedOfflineVersions.push(packgeEntity.releaseVersions[i]);
	    				}
	    			}
	    		};
	    		
	    		results.online.sort(function(a,b){return new Date(b.publishedAt) - new Date(a.publishedAt);});
	    		publishedOfflineVersions.sort(function(a,b){return new Date(b.publishedAt) - new Date(a.publishedAt);});
	    		nonPublishedOfflineVersions.sort(function(a,b){return new Date(b.createdAt) - new Date(a.createdAt);});
	    		results.offline = publishedOfflineVersions.concat(nonPublishedOfflineVersions);
	    		
	    		return results;
	    	};
	    	
	    	service.isEditableReleasePackage = function isEditableReleasePackage(releasePackage) {
	    		var userMember = Session.member;
	    		var memberMatches = angular.equals(userMember, releasePackage.member);
	    		//$log.log('isEditableReleasePackage', releasePackage, Session, memberMatches);
	    		return Session.isAdmin() || memberMatches;
	    	};
	    	
	    	service.isReleasePackageMatchingMember = function isReleasePackageMatchingMember(releasePackage) {
	    		var userMember = Session.member;
	    		var memberMatches = angular.equals(userMember, releasePackage.member);
	    		return memberMatches;
	    	};
	    	
	    	service.isEditableReleasePackage = function isEditableReleasePackage(releasePackage) {
	    		return this.isReleasePackageMatchingMember(releasePackage) || Session.isAdmin();
	    	};
	    	
	    	service.openExtensionApplication = function openExtensionApplication(releasePackage) {
	    		
	    		var modalInstance = $modal.open({
	    		      templateUrl: 'views/user/startExtensionApplicationModal.html',
	    		      size: 'sm'
	    		    });
	    		
	    		modalInstance.result.then(function () {
	    			UserRegistrationService.createExtensionApplication(UserAffiliateService.affiliate.affiliateId)
		    			.then(function(result) {
		    				if(result.data && result.data.applicationId) {
		    					$location.path('/extensionApplication/'+ result.data.applicationId);
		    				}
		    			})
		    			["catch"](function(message) {
		    				// FIXME: Should we show a global alert of some kind?
		    				$log.log(message);
		    			});
    		    }, function () {
    		    	//$log.info('Modal dismissed');
    		    });
	    		
	    	};
	    	
	    	service.orderIhtsdo = function(memberReleases) {
	    		return !(memberReleases.member && MemberService.isIhtsdoMember(memberReleases.member));
	    	};
	    	service.orderApprovedMemberships = function(memberReleases) {
	    		return !(memberReleases.member && _.some(UserAffiliateService.approvedMemberships, _.partial(MemberService.isMemberEqual, memberReleases.member)));
	    	};
	    	service.orderIncompleteMemberships = function(memberReleases) {
	    		return !(memberReleases.member && _.some(UserAffiliateService.incompleteMemberships, _.partial(MemberService.isMemberEqual, memberReleases.member)));
	    	};
	    	service.orderMemberName = function(memberReleases) {
	    		//FIXME use translated member name rather than key
	    		return  memberReleases.member && memberReleases.member.key || 'NONE';
	    	};

	    	service.releasePackageOrderBy = [service.orderIhtsdo, service.orderApprovedMemberships, service.orderIncompleteMemberships, service.orderMemberName];
	    	
			return service;
		} ]);
