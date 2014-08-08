'use strict';

mldsApp.controller('ImportAffiliatesController',
        [ '$scope', '$log', '$location', 'ImportAffiliatesService',
          function ($scope, $log, $location, ImportAffiliatesService) {
        	$scope.submitting = false;
        	$scope.alerts = [];
        	
        	$scope.uploadFile = function(){
        		$scope.alerts = [];
                var file = $scope.affiliatesFile;
                console.log('file is ' + JSON.stringify(file));
                ImportAffiliatesService.importAffiliates(file)
                	.then(function(result) {
                		$scope.alerts.push({type: 'success', msg: 'Import completed.'});
                		$scope.submitting = false;
                	})
        			["catch"](function(message) {
        				$log.log(message);
        				$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later. ['+ message.statusText+']'});
        				$scope.submitting = false;
        			});
            };
        }
    ]);