'use strict';

angular.module('MLDS').directive(
		'mavenVersion',
		[ '$http', 
		  '$log',
		  function($http, $log) {
			return {
				link : function(scope, element, attrs) {
					$http.get("/app/rest/version").success(function(versionInfo) {
						$log.log('mavenVersion', versionInfo);
						element.text(JSON.stringify(versionInfo));
					});
				}
			};
		} ]);