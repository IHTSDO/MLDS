'use strict';

mldsApp.controller('EditAffiliateController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'$routeParams',
		'$timeout',
		'Session',
		'AffiliateService',
		'ApplicationUtilsService',
		function($scope, $log, $location, $modal, $routeParams, $timeout, Session, AffiliateService, ApplicationUtilsService) {
			
			var affiliateId = $routeParams.affiliateId && parseInt($routeParams.affiliateId, 10);
			
			$scope.alerts = [];
			$scope.affiliate = {};
			$scope.affiliateDetails = {};
			$scope.approved = false;
			$scope.readOnly = false;
			$scope.type;

			function loadAffiliate() {
				var queryPromise = AffiliateService.affiliate(affiliateId);
				
				queryPromise.success(function(affiliate) {
					$scope.affiliate = affiliate;
					$scope.approved = ApplicationUtilsService.isApplicationApproved(affiliate.application);
					$scope.isEditable = Session.isAdmin() || (Session.member.key == affiliate.application.member.key);
					$scope.readOnly = !ApplicationUtilsService.isApplicationApproved(affiliate.application) || !$scope.isEditable;
					$scope.type = affiliate.type;
					
					if (affiliate.affiliateDetails) {
						$scope.affiliateDetails = affiliate.affiliateDetails;
					}
				});
					
			}

			loadAffiliate(); 
			
			$scope.form = {};
	    	$scope.form.attempted = false;
	    	
	    	$scope.save = function () {
	    		if ($scope.form.$invalid) {
	    			$scope.form.attempted = true;
	    			return;
	    		}

	    		$scope.submitting = true;
	    		$scope.alerts.splice(0, $scope.alerts.length);

	    		AffiliateService.updateAffiliateDetails($scope.affiliate.affiliateId, $scope.affiliateDetails)
	    			.success(function(result) {
	    				$scope.affiliateDetails = result.data;
	    				$scope.submitting = false;
	    				$location.path('/affiliates/'+ affiliateId);
	    				/* FIXME MB introduce an alerts service, put the alerts on the root scope, and prune 
	    				 * dismissed alerts on route change
	    				 */
	    				$timeout(function(){
		    				$scope.alerts.push({type: 'success', msg: 'Contact information has been successfully saved.'});
	    				});
	    			})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network request failure, please try again later.'});
					$scope.submitting = false;
				});
	        };
	        
	        $scope.cancel = function() {
	        	$location.path('/affiliates/'+ affiliateId);
	        };
			
		} ]);
