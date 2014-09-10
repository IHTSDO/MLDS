'use strict';

mldsApp.factory('ApplicationErrorCodeExtractor', [function() {
	return function extractErrorCodeFromMessage(message) {
	    var errorCodePatternResult = /MLDS_ERR_[A-Z_]+/.exec(message);
	    if (errorCodePatternResult) {
	    	return errorCodePatternResult[0];
	    } else {
	    	return null;
	    };
	};
}]);
