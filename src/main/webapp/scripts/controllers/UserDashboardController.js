'use strict';

//FIXME: Rename usersession and move into /scripts

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', '$modal', 'UserSession', 'CommercialUsageService', 'LicenseeService', 'Session', 'UserRegistrationService',
          function ($scope, $log, $location, $modal, UserSession, CommercialUsageService, LicenseeService, Session, UserRegistrationService) {
        	
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

        	$scope.licensees = [];

        	function loadLicensees() {
	        	LicenseeService.myLicensees()
	        		.then(function(licenseesResult) {
	        			$log.log(licenseesResult);
	        			$scope.licensees = licenseesResult.data;
	        			
	        			licenseesResult.data.forEach(function(licensee) {
	        				if (UserRegistrationService.isApplicationWaitingForApplicant(licensee.application)) {
	        					$location.path('/affiliateRegistration');
	        				}
	        				
	        				licensee.commercialUsages.sort(function(a, b) {
	        					if (a.startDate && b.startDate) {
	        						return new Date(b.startDate).getTime() - new Date(a.startDate).getTime();
	        					} else if (a.startDate) {
	        						return 1;
	        					} else {
	        						return -1;
	        					}
	        				});
	        			});
	
	        		});
        	}

        	loadLicensees();
        	
        	$scope.usageReportCountries = function(usageReport) {
        		return usageReport.countries.length;
        	};

        	$scope.usageReportHospitals = function(usageReport) {
        		return usageReport.entries.length;
        	};

        	$scope.usageReportPractices = function(usageReport) {
        		return usageReport.countries.reduce(function(total, count) {
        			return total + (count.practices || 0);
        		}, 0);
        	};

        	$scope.openAddUsageReportModal = function(licensee) {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/user/addUsageReportModal.html',
        			controller: 'AddUsageReportController',
        			size:'lg',
        			backdrop: 'static',
        			resolve: {
        				licenseeId: function() {
        					return licensee.licenseeId;
        				}
        			}
        		});
        	};

        	$scope.goToUsageReport = function(usageReport) {
        		$location.path('/usage-log/'+encodeURIComponent(usageReport.commercialUsageId));
        	};
        	
        	$scope.licenseeIsCommercial = function(licensee) {
        		return LicenseeService.licenseeIsCommercial(licensee);
        	};
        	
        	$scope.anySubmittedUsageReports = function(licensee) {
        		return _.some(licensee.commercialUsages, function(usageReport) {
        			return usageReport.approvalState !== 'NOT_SUBMITTED';
        		});
        	};
        	
        	$scope.isApplicationPending = function(application) {
        		return UserRegistrationService.isApplicationPending(application);
        	};
        	
        	$scope.isApplicationWaitingForApplicant = function(application) {
        		return UserRegistrationService.isApplicationWaitingForApplicant(application);
        	};
        	
        }
    ]);