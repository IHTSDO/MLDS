'use strict';

//FIXME: Rename usersession and move into /scripts

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', '$modal', 'UserSession', 'CommercialUsageService', 'LicenseeService', 'Session',
          function ($scope, $log, $location, $modal, UserSession, CommercialUsageService, LicenseeService, Session) {
        	
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

        	$scope.licensees = [];

        	LicenseeService.myLicensees()
        		.then(function(licenseesResult) {
        			$log.log(licenseesResult);
        			$scope.licensees = licenseesResult.data;
        			
        			licenseesResult.data.forEach(function(licensee) {
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
        	

        	
        	//FIXME: AC Seems to break when user refreshes page
        	UserSession.readyPromise.then(function(){
        		if (!UserSession.hasApplied()) {
        			$location.path('/affiliateRegistration');
        		} else if (!UserSession.isApproved()) {
        			//$location.path('/pendingRegistration');
        		} else {
        			// setup dashboard?
        		}
        	});
        	
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
        }
    ]);