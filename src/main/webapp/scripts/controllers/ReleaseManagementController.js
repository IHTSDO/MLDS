'use strict';

angular.module('MLDS').controller('ReleaseManagementController', 
		['$scope', '$log', '$modal',
    function ($scope, $log, $modal) {
        $scope.test =  "test variable";
        
        $scope.openModal = function() {
        	$log.log('button clicked');
        	
        	var modalInstance = $modal.open({
        	      templateUrl: 'views/admin/takeOfflineModal.html',
        	      controller: 'TakeOfflineModalController',
        	      scope: $scope,
        	      size: 'sm',
        	      windowClass: 'debugTest'
        	    });
        };
        
        
    }]);

