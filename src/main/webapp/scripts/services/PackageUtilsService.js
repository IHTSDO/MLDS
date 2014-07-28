'use strict';

angular.module('MLDS').factory('PackageUtilsService',
		[ '$resource', '$q', '$log', 'Session', function($resource, $q, $log, Session) {
			var service = {};
			
			service.isPackagePublished = function isPackagePublished(packageEntity) {
	        	for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
	        		if (packageEntity.releaseVersions[i].online) {
	        			return true;
	        		}
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
	        
	    	service.isAllowedToDownload = function isNotAllowedToDownload(releaseVersion) {
	    		for(var i = 0; i < releaseVersion.releaseFiles.length; i++) {
	    			if (releaseVersion.releaseFiles[i].downloadUrl)
	    				return true;
	    		};
	    		
	    		return false;
	    	};
	    	
	    	service.isEditableReleasePackage = function isEditableReleasePackage(releasePackage) {
	    		var userMember = Session.member;
	    		var memberMatches = angular.equals(userMember, releasePackage.member);
	    		//$log.log('isEditableReleasePackage', releasePackage, Session, memberMatches);
	    		return Session.isAdmin() || memberMatches;
	    	};
	    	
			return service;
		} ]);
