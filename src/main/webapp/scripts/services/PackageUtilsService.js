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
	        
	        service.isLatestOnlinePublishedVersion = function isLatestOnlinePublishedVersion(version, versions) {
	        	if (!version.online) {
	        		return false;
	        	}
	        	
	        	var onlineVersions = [];
	        	for(var i = 0; i < versions.length; i++) {
	        		if (versions[i].online) {
	        			onlineVersions.push(versions[i]);
	        		}
	        	};
	        	
	        	for(var i = 0; i < onlineVersions.length; i++) {
	        		if (onlineVersions[i].publishedAt && version.publishedAt && 
	    				(new Date(version.publishedAt) < new Date(onlineVersions[i].publishedAt) )) {
	    				return false;
	    			};
	        	};
	        	return true;
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
			
			return service;
		} ]);
