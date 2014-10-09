'use strict';

angular.module('MLDS').directive('input', function() {
	return {
		restrict : 'E',
		require : '?ngModel',
		link : function(scope, element, attr, ngModel) {
			// Only apply to <input type="tel">
			if (attr.type !== 'tel') {
				return;
			}

			element.intlTelInput({
				preferredCountries : []
			});

			var copyInputToModel = function() {
				var inputValue = element.val();
				ngModel.$setViewValue(inputValue);
			}
			element.on('focus blur keyup change', function() {
				scope.$apply(copyInputToModel);
			});
			copyInputToModel();
		}
	};
});
