'use strict';

angular.module('MLDS')
    .directive('dateAfterValidation', ['$log', '$filter', function($log, $filter) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function($scope, $element, $attrs, ctrl) {
            	var asMillis = function(value) {
            		if (angular.isNumber(value)) {
            			return value;
            		}
            		if (angular.isDate(value)) {
            			return value.getTime();
            		}
            		if (angular.isString(value)) {
            			return Date.parse(value.replace(/"/g, ""));
            		}
            		return new Date(value).getTime();
            	};
            	
            	 var validate = function(viewValue) {
            	        var comparisonModel = $attrs.dateAfterValidation;

            	        var viewMillis = asMillis(viewValue);
            	        var comparisonMillis = asMillis(comparisonModel);
            	        if(!viewValue || isNaN(viewMillis) || !comparisonModel || isNaN(comparisonMillis)){
            	          // It's valid because we have nothing to compare against
            	          ctrl.$setValidity('dateAfter', true);
            	        } else {
	            	      ctrl.$setValidity('dateAfter', comparisonMillis <= viewMillis);
            	        }
            	        
            	        return viewValue;
            	      };

        	      ctrl.$parsers.unshift(validate);
        	      ctrl.$formatters.push(validate);

        	      $attrs.$observe('dateAfterValidation', function(comparisonModel){
        	        return validate(ctrl.$viewValue);
        	      });
            }
        };
    }]);
