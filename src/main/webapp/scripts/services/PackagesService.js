'use strict';

angular.module('MLDS').factory('PackagesService',
		[ '$resource', '$q', function($resource, $q) {
			/*
			 * return $resource('app/rest/packages', {}, { });
			 */
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
					releaseVersionId : 23,
					name : "Package 2 Version 2 name",
					description : "Package 1 Version 2 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-05-01T13:55Z",
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
			} ];
			
			service.query = function query() {
				return datastore;
			};
			
			service.save = function(releasePackage) {
				//FIXME implement using call to server
				releasePackage.releasePackageId = new Date().getTime();
				releasePackage.createdAt = new Date().toUTCString();
				releasePackage.createdBy = 'admin';
				releasePackage.releaseVersions = [];
				datastore.push(releasePackage);
				return $q.when({data: releasePackage});
			};
			
			return service;
		} ]);
