'use strict';

angular.module('MLDS').factory('ReleaseVersionsService',
    ['$resource', function($resource) {
        return $resource(
            'api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId',
            {
                releasePackageId: '@releasePackageId',
                releaseVersionId: '@releaseVersionId'
            },
            {
                update: {
                    method: 'PUT'
                },
                notify: {
                    method: 'POST',
                    url: 'api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/notifications'
                },
                check: {
                    method: 'GET',
                    url: 'api/checkVersionDependency/:releaseVersionId'

                }
            }
        );
    }]
);
