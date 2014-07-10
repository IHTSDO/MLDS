'use strict';

angular.module('MLDS')
    .directive('unique', ['$log', function($log) {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attrs, ngModel) {
        	
        	var isUnique = function() {
				if (!ngModel || !element.val()) { return; };
				
				var values = scope.$eval(attrs.unique);
				
				if (values.indexOf(element.val()) != -1) {
					 ngModel.$setValidity('notUnique', false);
				} else {
					 ngModel.$setValidity('notUnique', true);
				};
	    	};
	    	
	    	scope.$watch(function () {
	            return ngModel.$modelValue;
	        }, isUnique);
	    	
	    	attrs.$observe('unique',isUnique);
        }
    };
}]);
