'use strict';

angular.module('MLDS')
    .directive('autoLink', ['$sce', function($sce) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function(scope, element, attrs, ngModel) {
                if (!ngModel) return;

                function linkifyTextNodes(node) {
                    if (node.nodeType === 3) { // Text node
                        var urlPattern = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
                        var text = node.nodeValue;
                        var newHtml = text.replace(urlPattern, function(url) {
                            return '<a href="' + url + '" target="_blank">' + url + '</a>';
                        });

                        if (newHtml !== text) {
                            var tempDiv = document.createElement('div');
                            tempDiv.innerHTML = newHtml;

                            var newNodes = Array.prototype.slice.call(tempDiv.childNodes);
                            newNodes.forEach(function(newNode) {
                                node.parentNode.insertBefore(newNode, node);
                            });

                            node.parentNode.removeChild(node);
                        }
                    } else if (node.nodeType === 1 && node.childNodes && !/^(a|script|style|textarea)$/i.test(node.tagName)) {
                        Array.prototype.slice.call(node.childNodes).forEach(linkifyTextNodes);
                    }
                }

                function linkifyHtml(html) {
                    if (!html) return html;

                    var tempDiv = document.createElement('div');
                    tempDiv.innerHTML = html;
                    Array.prototype.slice.call(tempDiv.childNodes).forEach(linkifyTextNodes);
                    return tempDiv.innerHTML;
                }

                function updateViewValue(value) {
                    var linkedValue = linkifyHtml(value);
                    if (linkedValue !== value) {
                        ngModel.$setViewValue(linkedValue);
                        ngModel.$render();
                    }
                    return linkedValue;
                }

                // Listen for model changes and update the view
                ngModel.$parsers.push(function(value) {
                    return updateViewValue(value);
                });

                // Listen for view changes and update the model
                ngModel.$formatters.push(function(value) {
                    return $sce.trustAsHtml(updateViewValue(value));
                });

                // Initial conversion
                scope.$watch(attrs.ngModel, function(newValue) {
                    if (newValue) {
                        updateViewValue(newValue);
                    }
                });
            }
        };
    }]);
