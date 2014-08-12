'use strict';

mldsApp.controller('AffiliateController', [
		'$scope',
		'$log',
		'$location',
		'$modal',
		'$routeParams',
		'Session',
		'AffiliateService',
		'ApplicationUtilsService',
		function($scope, $log, $location, $modal, $routeParams, Session, AffiliateService, ApplicationUtilsService) {
			
			var affiliateId = $routeParams.affiliateId && parseInt($routeParams.affiliateId, 10);
			
			$scope.alerts = [];
			$scope.affiliate = {};
			$scope.affiliateDetails = {};
			$scope.approved = false;
			$scope.readOnly = false;
			$scope.isApplicationApproved = ApplicationUtilsService.isApplicationApproved;

			function loadAffiliate() {
				var queryPromise = AffiliateService.affiliate(affiliateId);
				
				queryPromise.success(function(affiliate) {
					$scope.affiliate = affiliate;
					$scope.approved = $scope.isApplicationApproved(affiliate.application);
					$scope.isEditable = Session.isAdmin() || (Session.member.key == affiliate.application.member.key);
					$scope.readOnly = !$scope.isApplicationApproved(affiliate.application) || !$scope.isEditable;
					
					if (affiliate.affiliateDetails) {
						$scope.affiliateDetails = affiliate.affiliateDetails;
					}
				});
					
			}

			loadAffiliate(); 
			
			$scope.viewApplication = function(application) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/admin/applicationSummaryModal.html',
        			controller: 'ApplicationSummaryModalController',
        			size:'lg',
        			resolve: {
        				application: function() {
        					return application;
        				}
        			}
        		});
			};
			
			$scope.approveApplication = function(application) {
				$location.path('/applicationReview/' + application.applicationId);
			};
			
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
	    			.then(function(result) {
	    				$scope.affiliateDetails = result.data;
	    				$scope.submitting = false;
	    				$scope.alerts.push({type: 'success', msg: 'Contact information has been successfully saved.'});
	    			})
				["catch"](function(message) {
					$scope.alerts.push({type: 'danger', msg: 'Network failure, please try again later.'});
					$scope.submitting = false;
				});
	        };
	        
	        $scope.cancel = function() {
	        	//FIXME $route.reload wasnt clearing out scope state - is there a better way?
	        	window.location.reload();
	        };
			
		} ]);
