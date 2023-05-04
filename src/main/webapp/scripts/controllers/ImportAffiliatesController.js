'use strict';

mldsApp.controller('ImportAffiliatesController',
        [ '$scope', '$log', '$location', 'ImportAffiliatesService', 'AuditsService',
          function ($scope, $log, $location, ImportAffiliatesService, AuditsService) {
        	$scope.submitting = false;
        	$scope.alerts = [];
        	$scope.importResult = null;
        	$scope.importSpec = null;
        	
        	$scope.exportAffiliatesUrl = ImportAffiliatesService.exportAffiliatesUrl;
        	
        	$scope.uploadFile = function(){
        		$scope.alerts = [];
        		$scope.importResult = null;
                var file = $scope.affiliatesFile;
                $scope.submitting = true;
                ImportAffiliatesService.importAffiliates(file)
                	.then(function(result) {
                		$scope.importResult = JSON.stringify(result.data, undefined, 2);
                		$scope.alerts.push({type: 'success', msg: 'Import completed.'});
                		$scope.submitting = false;
                		loadAudits();
                	})
        			["catch"](function(message) {
        				$log.log(message);
        				$scope.importResult = JSON.stringify(message.data, undefined, 2);
        				$scope.alerts.push({type: 'danger', msg: 'Network request failure [14]: please try again later.'});
        				$scope.submitting = false;
                		loadAudits();
        			});
            };
            
            $scope.audits = [];
            
            loadAudits();
            loadImportSpec();
            
            function loadAudits() {
            	AuditsService.findByAuditEventType('AFFILIATE_IMPORT')
            	.then(function(result) {
            		$scope.audits = result;
            	})
    			["catch"](function(message) {
    				$log.log('Failed to update audit list: '+message);
    			});
            }
            
            function loadImportSpec() {
            	ImportAffiliatesService.importSpec()
            	.then(function(result) {
            		$log.log(result);
            		$scope.importSpec = result.data;
            	})
    			["catch"](function(message) {
    				$log.log("Failed to load import spec"+ message);
    			});

            };
        }
    ]);