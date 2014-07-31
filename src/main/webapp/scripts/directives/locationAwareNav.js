'use strict';

angular.module('MLDS')
    .directive('locationAwareNav', ['$location', '$rootScope', '$timeout', function($location, $rootScope, $timeout) {
    	function link(scope, element, attrs){
    		function updateActiveFlag(){
                var links = element.find('a');
                var classSelected = "active";
                var currentPath = $location.path();
                
                //console.log('path change', currentPath, element, links.size(), links);
                links.closest('li').removeClass(classSelected);
                
                angular.forEach(links, function(value){
                    var a = angular.element(value);
                    var navHref = a.attr('href') ? a.attr('href') : '';
                    
                    var pathParts = currentPath.split('/');
                    
                    function isCurrent(currentPath) {
                    	return ((currentPath != "" && currentPath != "/") && navHref.indexOf("#/"+currentPath) == 0); 
                    }
                
                    if (pathParts.length > 1) {
                    	angular.forEach(pathParts, function(pathPart) {
                    		if (isCurrent(pathPart)){
                    			//console.log('nav - chose', value);
                    			a.closest('li').addClass(classSelected);
                    		}
                    	});
                    } else if ((currentPath == "" || currentPath == "/") && navHref == "#") { //home page special case
                		a.closest('li').addClass(classSelected);
                    }
                    	 
                });
            }
    		
    		scope.$watch(function() {
                return $location.path();
            }, updateActiveFlag);
    		$rootScope.$watch('authenticated', function(){ $timeout(updateActiveFlag);});
        }
        return {link: link};
    }]);
