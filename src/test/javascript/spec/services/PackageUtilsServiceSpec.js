'use strict';

describe('PackageUtilsService Tests ', function () {

    beforeEach(module('MLDS'));
    
    describe('package service utilities', function () {
        var PackageUtilsService;
        
        var datastore = {
    		releaseVersions : [ {
    		    name: "version 0",
    			createdAt : "2014-01-01T13:55Z",
    			online: false,
    		}, {
    		    name: "version 1",
    			createdAt : "2014-05-01T13:55Z",
    			online: true,
    			publishedAt : "2014-05-01T14:55Z",
    		}, {
    		    name: "version 2",
    			createdAt : "2014-11-01T13:55Z",
    			online: true,
    			publishedAt : "2014-11-01T14:55Z",
    		}, {
    		    name: "version 3",
    			createdAt : "2014-05-01T13:55Z",
    			online: false,
    			publishedAt : "2014-01-01T14:55Z",
    		}, {
    		    name: "version 4",
    			createdAt : "2014-06-01T13:55Z",
    			online: false,
    			publishedAt : "2014-08-01T14:55Z",
    		}, {
    		    name: "version 5",
    			createdAt : "2014-08-01T13:55Z",
    			online: false,
    		} ]
    	};

        beforeEach(inject(function (_PackageUtilsService_) {
            PackageUtilsService = _PackageUtilsService_;
        }));

        it('isPackagePublished should be true when any version is "online"', function () {
            expect(PackageUtilsService.isPackagePublished({releaseVersions:[{online:false}, {online:true}]})).toBeTruthy();
        });
        it('isPackagePublished should be false when no version is "online"', function () {
            expect(PackageUtilsService.isPackagePublished({releaseVersions:[{online:false}, {}]})).not.toBeTruthy();
        });
        it('isLatestVersion should be true when version is the latest published version', function () {
            expect(PackageUtilsService.isLatestVersion(datastore.releaseVersions[2], datastore.releaseVersions)).toBeTruthy();
        });
        it('isLatestVersion should be false when version is not the latest published version', function () {
            expect(PackageUtilsService.isLatestVersion(datastore.releaseVersions[0], datastore.releaseVersions)).not.toBeTruthy();
        });
        it('getLatestPublishedDate should return the latest published date', function () {
            expect(PackageUtilsService.getLatestPublishedDate(datastore)).toBe(datastore.releaseVersions[2].publishedAt);
        });

        it('updateVersionsLists should sort online version by publishedAt dates in descending order', function(){
            var results = PackageUtilsService.updateVersionsLists(datastore);
            
            expect(results.online.length).toBe(2);
            expect(results.online[0]).toBe(datastore.releaseVersions[2]);
            expect(results.online[1]).toBe(datastore.releaseVersions[1]);
        });
        
        it('should sort offline version by publishedAt dates in descending order and then sort by createdAt in desending order', function(){
            var results = PackageUtilsService.updateVersionsLists(datastore);
            
            expect(results.offline.length).toBe(4);
            expect(results.offline[0]).toBe(datastore.releaseVersions[4]);    
            expect(results.offline[1]).toBe(datastore.releaseVersions[3]);    
            expect(results.offline[2]).toBe(datastore.releaseVersions[5]);
            expect(results.offline[3]).toBe(datastore.releaseVersions[0]);    
        });
    });
    
    describe('package authorization utilities', function () {
        var PackageUtilsService, Session, USER_ROLES, isAdmin,ihtsdoPackageEntity,swedenPackageEntity,
        	ihtsdoMember = { key:'IHTSDO' },
        	swedenMember = { key:'SE' };
        
        Session = {
        	isAdmin: function() {return isAdmin;}
        };
        
        ihtsdoPackageEntity = {
        	name: "Package name",
        	versions:[],
        	member:ihtsdoMember
        };
        swedenPackageEntity = {
        		name: "SE Package name",
        		versions:[],
        		member:swedenMember
        };
        
        beforeEach(module(function($provide) {
        	  $provide.value("Session", Session);
        	}));
        
        beforeEach(inject(function (_PackageUtilsService_, _USER_ROLES_) {
            PackageUtilsService = _PackageUtilsService_;
            USER_ROLES = _USER_ROLES_;
        }));

        it('Admin can edit all packages', function(){
        	isAdmin = true;
        	expect(PackageUtilsService.isEditableReleasePackage(ihtsdoPackageEntity)).toBeTruthy();
        	expect(PackageUtilsService.isEditableReleasePackage(swedenPackageEntity)).toBeTruthy();
        });
        it('Staff can only edit package with matching member', function(){
        	isAdmin = false;
        	Session.member = swedenMember;
        	
        	expect(PackageUtilsService.isEditableReleasePackage(ihtsdoPackageEntity)).not.toBeTruthy();
        	expect(PackageUtilsService.isEditableReleasePackage(swedenPackageEntity)).toBeTruthy();
        });

    });

});
