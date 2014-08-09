'use strict';

mldsApp.controller('ImportAffiliatesController',
        [ '$scope', '$log', '$location', 'ImportAffiliatesService',
          function ($scope, $log, $location, ImportAffiliatesService) {
        	$scope.submitting = false;
        	$scope.alerts = [];
        	$scope.importResult = null;
        	
        	$scope.uploadFile = function(){
        		$scope.alerts = [];
        		$scope.importResult = null;
                var file = $scope.affiliatesFile;
                console.log('file is ' + JSON.stringify(file));
                ImportAffiliatesService.importAffiliates(file)
                	.then(function(result) {
                		$scope.importResult = result.data;
                		$scope.alerts.push({type: 'success', msg: 'Import completed.'});
                		$scope.submitting = false;
                	})
        			["catch"](function(message) {
        				$log.log(message);
        				$scope.importResult = message.data;
        				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later. ['+ message.statusText+']'});
        				$scope.submitting = false;
        			});
            };
        }
    ]);