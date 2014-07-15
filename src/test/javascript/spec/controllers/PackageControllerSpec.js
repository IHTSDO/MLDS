'use strict';

describe('PackageController Tests ', function () {

    beforeEach(module('MLDS'));
    
    describe('package utilities', function () {
        var $scope;
        
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

        beforeEach(inject(function ($rootScope, $controller) {
            $scope = $rootScope.$new();
            $controller('PackageController', {$scope: $scope});
        }));

        it('should sort online version by publishedAt dates in descending order', function(){
            var results = $scope.updateVersionsLists(datastore);
            
            expect(results.online.length).toBe(2);
            expect(results.online[0]).toBe(datastore.releaseVersions[2]);
            expect(results.online[1]).toBe(datastore.releaseVersions[1]);
            
        });
        
        it('should sort offline version by publishedAt dates in descending order and then sort by createdAt in desending order', function(){
            var results = $scope.updateVersionsLists(datastore);
            
            expect(results.offline.length).toBe(4);
            expect(results.offline[0]).toBe(datastore.releaseVersions[4]);    
            expect(results.offline[1]).toBe(datastore.releaseVersions[3]);    
            expect(results.offline[2]).toBe(datastore.releaseVersions[5]);
            expect(results.offline[3]).toBe(datastore.releaseVersions[0]);    
        });
    });
});
