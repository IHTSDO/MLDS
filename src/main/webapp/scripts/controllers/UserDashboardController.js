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
        	if (!UserSession.hasApplied()) {
        		$location.path('/affiliateRegistration');
        	} else if (!UserSession.isApproved()) {
        		$location.path('/pendingRegistration');
        	} else {
        		// setup dashboard?
        	}
        	
        	$scope.usageReportCountries = function(usageReport) {
        		var uniqueCountryCodes = [];
        		
        		usageReport.entries.forEach(function(entry) {
        			if (uniqueCountryCodes.indexOf(entry.country.isoCode2) === -1) {
        				uniqueCountryCodes.push(entry.country.isoCode2);
        			}
        		});
        		usageReport.counts.forEach(function(count) {
        			//FIXME better way to detect that there is some use within a country
        			if (count.practices > 0
        					&& uniqueCountryCodes.indexOf(count.country.isoCode2) === -1) {
        				uniqueCountryCodes.push(count.country.isoCode2);
        			}
        		});

        		return uniqueCountryCodes.length;
        	};

        	$scope.usageReportHospitals = function(usageReport) {
        		return usageReport.entries.length;
        	};

        	$scope.usageReportPractices = function(usageReport) {
        		return usageReport.counts.reduce(function(total, count) {
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
        		$location.path('/usage-log/'+usageReport.commercialUsageId);
        	};
        }
    ]);