'use strict';

describe('PackageManagementController Tests ', function () {

    beforeEach(module('MLDS'));
    
    describe('package utilities', function () {
        var $scope;
        
        var datastore = [
             	{
					releaseVersions : [ {
						createdAt : "2014-01-01T13:55Z",
						online: true,
						publishedAt : "2014-01-01T14:55Z"
					} ]
				}, {
					releaseVersions : [ {
						createdAt : "2014-01-01T13:55Z",
						online: false,
						publishedAt : "2014-01-01T14:55Z"
					}, {
						createdAt : "2014-05-01T13:55Z",
						online: true,
						publishedAt : "2014-05-01T14:55Z"
					}, {
						createdAt : "2014-11-01T13:55Z",
						online: true,
						publishedAt : "2014-11-01T14:55Z"
					} ]
				}];

        beforeEach(inject(function ($rootScope, $controller) {
            $scope = $rootScope.$new();
            $controller('PackageManagementController', {$scope: $scope});
        }));

        it('isPackagePublished should be true when any version is "online"', function () {
            expect($scope.isPackagePublished({releaseVersions:[{online:false}, {online:true}]})).toBeTruthy();
        });
        it('isPackagePublished should be false when no version is "online"', function () {
            expect($scope.isPackagePublished({releaseVersions:[{online:false}, {}]})).not.toBeTruthy();
        });
        it('isLatestOnlinePublishedVersion should be true when version is the latest published version', function () {
            expect($scope.isLatestOnlinePublishedVersion(datastore[1].releaseVersions[2], datastore[1].releaseVersions)).toBeTruthy();
        });
        it('isLatestOnlinePublishedVersion should be false when version is not the latest published version', function () {
            expect($scope.isLatestOnlinePublishedVersion(datastore[1].releaseVersions[0], datastore[1].releaseVersions)).not.toBeTruthy();
        });
        it('getLatestPublishedDate should return the latest published date', function () {
            expect($scope.getLatestPublishedDate(datastore[1])).toBe(datastore[1].releaseVersions[2].publishedAt);
        });
        
    });
});
