'use strict';

angular.module('MLDS')
    .directive('locationAwareNav', ['$location', function($location) {
    	function link(scope, element, attrs){
            scope.$watch(function() {
                return $location.path();
            }, function(newPath){
                var links = element.find('a');
                var classSelected = "active";
                
                //console.log('path change', newPath, links.size());
                links.closest('li').removeClass(classSelected);
                
                angular.forEach(links, function(value){
                	//console.log('looking at', value);
                    var a = angular.element(value);
                    var navHref = a.attr('href');
                    var isCurrent = (newPath != "" && navHref.indexOf("#"+newPath) == 0)
                    	|| (newPath == "" && navHref == "#"); //home page special case
                    	 
                    if (isCurrent){
                        a.closest('li').addClass(classSelected);
                    }
                });
            });
        }
        return {link: link};
    }]);
