angular.module('MLDS').filter('enum', [
  '$parse',
  '$translate',
  function ($parse, $translate) {
    return function (enumValue, translateKeyPrefix) {
    	var combinedKey = translateKeyPrefix + enumValue,
    		result = null;
    	if (enumValue != null && enumValue != "") {
    		result = $translate.instant(combinedKey);
    	}
    	return result;
    };
  }
]);
