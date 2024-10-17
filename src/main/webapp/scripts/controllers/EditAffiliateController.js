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
		'CountryService',
		function($scope, $log, $location, $modal, $routeParams, $timeout, Session, AffiliateService, ApplicationUtilsService, CountryService) {

			var affiliateId = $routeParams.affiliateId && parseInt($routeParams.affiliateId, 10);

			$scope.alerts = [];
			$scope.affiliate = {};
			$scope.affiliateDetails = {};
			$scope.approved = false;
			$scope.readOnly = false;
			$scope.type;
	        $scope.availableCountries = CountryService.countries;
	        $scope.loading = true;

	    	$scope.agreementTypeOptions = ['AFFILIATE_NORMAL', 'AFFILIATE_RESEARCH', 'AFFILIATE_PUBLIC_GOOD'];

			function loadAffiliate() {
				var queryPromise = AffiliateService.affiliate(affiliateId);

				queryPromise.success(function(affiliate) {
					$scope.affiliate = affiliate;
					$scope.approved = ApplicationUtilsService.isApplicationApproved(affiliate.application);
					$scope.isEditable = Session.isAdmin() || (Session.member.key == affiliate.application.member.key);
					$scope.readOnly = !ApplicationUtilsService.isApplicationApproved(affiliate.application) || !$scope.isEditable;
					$scope.type = affiliate.type || affiliate.affiliateDetails.type;
					$scope.loading = false;

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
	    				$location.path('/affiliateManagement/'+ affiliateId);
	    				/* FIXME MB introduce an alerts service, put the alerts on the root scope, and prune
	    				 * dismissed alerts on route change
	    				 */
	    				$timeout(function(){
		    				$scope.alerts.push({type: 'success', msg: 'Contact information has been successfully saved.'});
	    				});
	    			})
				["catch"](function(message) {
                    let errorMessage = 'Edit record failure: If this user exists already then the update will not be successful.  Please ensure when changing emails that the user is not already in the system.  ';

                    if (message && message.data && message.data.message) {
                        errorMessage += "Message=" + message.data.message;
                    }

                    if (message && message.statusText) {
                        errorMessage += "Status Test=" + message.statusText;
                    }

                    $scope.alerts.push({type: 'danger', msg: errorMessage});
					$scope.submitting = false;
				});
	        };

	        $scope.cancel = function() {
	        	$location.path('/affiliateManagement/'+ affiliateId);
	        };

		} ]);
