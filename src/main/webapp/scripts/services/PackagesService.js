'use strict';

angular.module('MLDS').factory('PackagesService',
		[ '$resource', function($resource) {
			/*
			 * return $resource('app/rest/packages', {}, { });
			 */
			var service = {};
			service.query = function query() {
				return [ {
					packageId : 1,
					name : "Package 1 title",
					description : "Package 1 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T12:55Z",
					releaseVersions : [ {
						versionId : 11,
						name : "Package 1 Version 1 name",
						description : "Package 1 Version 1 description",
						createdBy : "michael.buckley@intelliware.com",
						createdAt : "2014-01-01T13:55Z",
						publishedAt : "2014-01-01T14:55Z",
						releaseFiles : [ {
							fileId : 13,
							label : "Complete File",
							downloadUrl : "http://example.com/bigFile.zip"
						}, {
							fileId : 14,
							label : "Delta File",
							downloadUrl : "http://example.com/smallerFile.zip"
						} ]
					} ]
				}, {
					packageId : 2,
					name : "Package 2 title",
					description : "Package 2 description",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T12:55Z",
					releaseVersions : [ {
						versionId : 22,
						name : "Package 2 Version 1 name",
						description : "Package 1 Version 1 description",
						createdBy : "michael.buckley@intelliware.com",
						createdAt : "2014-01-01T13:55Z",
						publishedAt : "2014-01-01T14:55Z",
						releaseFiles : [ {
							fileId : 23,
							label : "Complete File",
							downloadUrl : "http://example.com/bigFile.zip"
						}, {
							fileId : 24,
							label : "Delta File",
							downloadUrl : "http://example.com/smallerFile.zip"
						} ]
					}, {
						versionId : 23,
						name : "Package 2 Version 2 name",
						description : "Package 1 Version 2 description",
						createdBy : "michael.buckley@intelliware.com",
						createdAt : "2014-05-01T13:55Z",
						publishedAt : "2014-05-01T14:55Z",
						releaseFiles : [ {
							fileId : 231,
							label : "Complete File",
							downloadUrl : "http://example.com/bigFile.zip"
						}, {
							fileId : 241,
							label : "Delta File",
							downloadUrl : "http://example.com/smallerFile.zip"
						} ]
					} ]
				}, {
					packageId : 3,
					name : "Package 3 title",
					description : "Package 3 description not published",
					createdBy : "michael.buckley@intelliware.com",
					createdAt : "2014-01-01T12:55Z",
					releaseVersions : [ {
						versionId : 15,
						name : "Package 3 Version 1 name",
						description : "Package 1 Version 1 description",
						createdBy : "michael.buckley@intelliware.com",
						createdAt : "2014-01-01T13:55Z",
						releaseFiles : [ {
							fileId : 313,
							label : "Complete File",
							downloadUrl : "http://example.com/bigFile.zip"
						}, {
							fileId : 314,
							label : "Delta File",
							downloadUrl : "http://example.com/smallerFile.zip"
						} ]
					} ]
				} ];
			};
			return service;
		} ]);
