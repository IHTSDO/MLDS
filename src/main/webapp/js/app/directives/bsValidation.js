angular.module('MLDS')
  .directive('bsValidation', ['$log', function($log) {
    return {
        restrict: "A",
        link: function(scope, element, attrs, ctrl) {

            if (element.get(0).nodeName.toLowerCase() === 'form') {
            	//$log.log('bsValidation - wiring .form-group in form');
                element.find('.form-group').each(function(i, formGroup) {
                    showValidation(angular.element(formGroup));
                });
            } else {
                showValidation(element);
            }

            function showValidation(formGroupEl) {
                var input = formGroupEl.find('input[ng-model],textarea[ng-model],select[ng-model]');
                if (input.length > 0) {
                    scope.$watch(function() {
                        return input.hasClass('ng-invalid');
                    }, function(isInvalid) {
                    	//$log.log('bsValidation - in $watch callback', isInvalid, formGroupEl, input);
                        formGroupEl.toggleClass('has-error', isInvalid);
                    });
                }
            }
        }
    };
}]);
