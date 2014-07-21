'use strict';

angular.module('MLDS').factory('PackageUtilsService',
		[ '$resource', '$q', '$log', function($resource, $q, $log) {
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
	    		var latestPublishDate = new Date(); 
	    		for(var i = 0; i < packageEntity.releaseVersions.length; i++) {
	    			if (i == 0) {
	    				latestPublishDate = packageEntity.releaseVersions[i].publishedAt;
	    			} else if (new Date(packageEntity.releaseVersions[i].publishedAt) > new Date(latestPublishDate) ) {
	    				latestPublishDate = packageEntity.releaseVersions[i].publishedAt;
	    			};
	    		};
	    		return latestPublishDate;
	        };
			
	        service.isVersionOnline = function isVersionOnline(releaseVersion) {
	        	return releaseVersion.online ? true : false;
	        };
	        
	        service.isLatestVersion = function isLatestVersion(version, versions) {
	        	for(var i = 0; i < versions.length; i++) {
	        		if (versions[i].publishedAt && version.publishedAt && 
	    				(new Date(version.publishedAt) < new Date(versions[i].publishedAt) )) {
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
	    	
			return service;
		} ]);
