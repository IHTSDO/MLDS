'use strict';

angular.module('MLDS', ['ui.bootstrap']);
var ToolTip = function ($scope) {
  $scope.dynamicPopover = 'Hello, World!';
  $scope.dynamicPopoverTitle = 'Title';
};