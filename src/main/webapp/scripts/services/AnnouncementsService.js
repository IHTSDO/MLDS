'use strict';

angular.module('MLDS').factory('AnnouncementsService',
    ['$resource',
	 function($resource) {
		return $resource(
				'api/announcements',
				{
				}, {
					post : {
						method : 'POST'
					}
				});
} ]);
