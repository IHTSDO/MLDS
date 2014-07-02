'use strict';

angular.module('MLDS')
    .directive('autoSubmit', ['$log', function($log) {
    return {
        restrict: 'A',
        scope: false,
        require: ['^form'],
        link: function (scope, element, attrs, formCtrl) {

        	var changed = false;
            	
                scope.$watch(attrs.ngModel, function (newValue, oldValue) {
                    if (newValue !== oldValue) {
                    	changed = true;
                    }
                });
                element.bind('blur', function() {
                	if (changed) {
                		//FIXME assumed name from scope
                		scope.submit();
                	}
                	changed = false;
                  });
                
//        	var changed = false;
//            scope.$watch(attrs.ngModel, function (newValue, oldValue) {
//                if (newValue !== oldValue) {
//                	changed = true;
//                }
//            });
//            element.bind('blur', function() {
//            	$log.log('blur '+changed);
//            	changed = false;
//              });
        }
    };
}]);
