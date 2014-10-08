'use strict';

angular.module('MLDS').directive('input', function() {
    return {
        restrict: 'E',
        require: '?ngModel',
        link: function (scope, element, attr, ngModel) {
          if (attr.type !== 'tel') return;

          var read = function() {
              var inputValue = element.val();
              ngModel.$setViewValue(inputValue);
            }      
            element.intlTelInput({
            	preferredCountries:[]
            });
            element.on('focus blur keyup change', function() {
                scope.$apply(read);
            });
            read();          
        }
    };
});
