'use strict';

angular.module('MLDS').directive(
		'match',
		[ function() {
			return {
				restrict: 'A',
				require : '?ngModel',
				link : function(scope, elem, attrs, ctrl) {
					if(!ctrl) {
						return;
					}

					scope.$watch(
							'[' + attrs.ngModel + ', ' + attrs.match + ']',
							function(value) {
								ctrl.$setValidity('match', value[0] === value[1]);
							}, true);

				}
			};
		} ]);