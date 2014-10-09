'use strict';

angular.module('MLDS').directive('input', function() {
	return {
		restrict : 'E',
		require : '?ngModel',
		priority: 1, // otherwise the input directive replaces our $render
		link : function(scope, element, attr, ngModel) {
			// Only apply to <input type="tel"> and bound elements
			if (attr.type !== 'tel' ||
				!ngModel) {
				return;
			}

			var telCountryModel = attr['telCountryModel'],
				copyInputToModel;
			
			element.intlTelInput({
				preferredCountries : []
			});
			
			ngModel.$render = function() {
				element.intlTelInput("setNumber", ngModel.$viewValue||'');
			};
			
			copyInputToModel = function() {
				var inputValue = element.val();
				ngModel.$setViewValue(inputValue);
			}
			
			element.on('focus blur keyup change', function() {
				scope.$apply(copyInputToModel);
			});
			
			if (telCountryModel) {
				scope.$watch(telCountryModel, function(isoCode2){
					// default the country if there is no content yet.
					if (!element.val() && isoCode2) {
						element.intlTelInput("selectCountry", isoCode2.toLowerCase());
					}
				});
			}
		}
	};
});
