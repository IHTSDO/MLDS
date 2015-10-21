'use strict';

angular.module('MLDS').factory('AnnouncementsService',
    ['$resource',
	 function($resource) {
		return $resource(
				'app/rest/announcements',
				{
				}, {
					post : {
						method : 'POST'
					}
				});
} ]);
