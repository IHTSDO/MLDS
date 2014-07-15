'use strict';

angular.module('MLDS').factory('ReleaseVersionsService',
    ['$resource',
	 function($resource) {
		return $resource(
				'app/rest/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId',
				{
					releasePackageId : '@releasePackageId',
					releaseVersionId : '@releaseVersionId'
				}, {
					update : {
						method : 'PUT'
					}
				});
} ]);
