'use strict';

angular.module('MLDS').factory('ReleaseFilesService',
    ['$resource',
	 function($resource) {
		return $resource(
				'api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/releaseFiles/:releaseFileId',
				{
					releasePackageId : '@releasePackageId',
					releaseVersionId : '@releaseVersionId',
					releaseFileId : '@releaseFileId'
				}, {
					update : {
						method : 'PUT',
						url: 'api/releasePackages/:releasePackageId/releaseVersions/:releaseVersionId/releaseFiles'
					}
				});
} ]);
