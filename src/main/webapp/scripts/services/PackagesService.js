'use strict';

angular.module('MLDS').factory('PackagesService',
		[ '$resource', '$q', '$log', function($resource, $q, $log) {
			var service = {};
			
			var datastore = [ {
				releasePackageId : 1,
				name : "Package 1 title",
				description : "Package 1 description",
				createdBy : "michael.buckley@intelliware.com",
				createdAt : "2014-01-01T12:55Z",
				releaseVersions : [ {
					releaseVersionId : 11,
					name : "Package 1 Version 1 name",
					description : "Package 1 Version 1 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T13:55Z",
					online: true,
					publishedAt : "2014-01-01T14:55Z",
					releaseFiles : [ {
						releaseFileId : 13,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 14,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				} ]
			}, {
				releasePackageId : 2,
				name : "Package 2 title",
				description : "Package 2 description",
				createdBy : "michael.buckley@intelliware.com",
				createdAt : "2014-01-01T12:55Z",
				releaseVersions : [ {
					releaseVersionId : 22,
					name : "Package 2 Version 1 name",
					description : "Package 1 Version 1 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T13:55Z",
					online: false,
					releaseFiles : [ {
						releaseFileId : 23,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 24,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				}, {
					releaseVersionId : 23,
					name : "Package 2 Version 2 name",
					description : "Package 1 Version 2 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-05-01T13:55Z",
					online: true,
					publishedAt : "2014-05-01T14:55Z",
					releaseFiles : [ {
						releaseFileId : 231,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 241,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				}, {
					releaseVersionId : 23,
					name : "Package 2 Version 3 name",
					description : "Package 1 Version 3 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-11-01T13:55Z",
					online: true,
					publishedAt : "2014-11-01T14:55Z",
					releaseFiles : [ {
						releaseFileId : 231,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 241,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				}, {
					releaseVersionId : 22,
					name : "Package 2 Version 4 name",
					description : "Package 2 Version 4 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-05-01T13:55Z",
					online: false,
					publishedAt : "2014-01-01T14:55Z",
					releaseFiles : [ {
						releaseFileId : 23,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 24,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				}, {
					releaseVersionId : 24,
					name : "Package 2 Version 5 name",
					description : "Package 2 Version 5 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-06-01T13:55Z",
					online: false,
					publishedAt : "2014-08-01T14:55Z",
					releaseFiles : [ {
						releaseFileId : 23,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 24,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				}, {
					releaseVersionId : 24,
					name : "Package 2 Version 6 name",
					description : "Package 2 Version 6 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-08-01T13:55Z",
					online: false,
					releaseFiles : [ {
						releaseFileId : 23,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 24,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				} ]
			}, {
				releasePackageId : 3,
				name : "Package 3 title",
				description : "Package 3 description not published",
				createdBy : "michael.buckley@intelliware.com",
				createdAt : "2014-01-01T12:55Z",
				releaseVersions : [ {
					releaseVersionId : 15,
					name : "Package 3 Version 1 name",
					description : "Package 1 Version 1 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T13:55Z",
					releaseFiles : [ {
						releaseFileId : 313,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 314,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				} ]
			}, {
				releasePackageId : 4,
				name : "Package 4 title",
				description : "Package 4 description not published",
				createdBy : "michael.buckley@intelliware.com",
				createdAt : "2014-02-01T12:55Z",
				releaseVersions : [ {
					releaseVersionId : 15,
					name : "Package 4 Version 1 name",
					description : "Package 4 Version 1 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T13:55Z",
					releaseFiles : [ {
						releaseFileId : 313,
						label : "Complete File",
						downloadUrl : "http://example.com/bigFile.zip"
					}, {
						releaseFileId : 314,
						label : "Delta File",
						downloadUrl : "http://example.com/smallerFile.zip"
					} ]
				} ]
			} ];
			
			service.query = function query() {
				return datastore;
			};
			
			service.save = function(releasePackage) {
				releasePackage.releasePackageId = new Date().getTime();
				releasePackage.createdAt = new Date().toUTCString();
				releasePackage.createdBy = 'admin';
				releasePackage.releaseVersions = [];
				datastore.push(releasePackage);
				releasePackage.$promise = $q.when(releasePackage);
				return releasePackage;
			};

			service['delete'] = function(releasePackage) {
				var response = {};
				var index = findReleasePackageIndex(releasePackage);
				if (index !== -1) {
					datastore.splice(index, 1);
					response.$promise = $q.when({});
				} else {
					var deferred = $q.defer();
					response.$promise = deferred.promise;
					deferred.reject('not found');
				}
				return response;
			};
			
			function findReleasePackageIndex(releasePackage) {
				for (var i = 0; i < datastore.length; i++) {
					if (datastore[i].releasePackageId === releasePackage.releasePackageId) {
						return i;
					}
				}
				return -1;
			}
			
			service.update = function(releasePackageCopy) {
				var index = findReleasePackageIndex(releasePackageCopy);
				if (index !== -1) {
					datastore[index] = releasePackageCopy;
				} else {
					$log.log('Update release package that doesnt exist!');
					datastore.push(releasePackageCopy);
				}
				releasePackageCopy.$promise = $q.when(releasePackageCopy);
				return releasePackageCopy;
			};
			
			service.get = function(match) {
				var found = _.find(datastore, function(releasePackage) {
					return releasePackage.releasePackageId === match.releasePackageId;
				});
				if (found) {
					$log.log('found match');
					found.$promise = $q.when(found);
					return found;
				} else {
					var missing = {};
					var deferred = $q.defer();
					missing.$promise = deferred.promise;
					deferred.reject('not found');
					return missing;
				}
			};
			

			var fakeMode = false;
			if (fakeMode) {
				return service;
			} else {
				return $resource('app/rest/releasePackages/:releasePackageId', {releasePackageId: '@releasePackageId'}, {
					update: {method: 'PUT'}
				});
			}
		} ]);
