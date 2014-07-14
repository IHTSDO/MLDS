'use strict';

describe('PackageManagementController Tests ', function () {

    beforeEach(module('MLDS'));

    describe('package utilities', function () {
        var $scope;

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
    });
});
