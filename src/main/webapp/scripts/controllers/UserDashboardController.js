'use strict';

//FIXME: Rename usersession and move into /scripts

angular.module('MLDS')
    .controller('UserDashboardController',
        [ '$scope', '$log', '$location', '$modal', 'UserSession', 'CommercialUsageService', 'Session',
          function ($scope, $log, $location, $modal, UserSession, CommercialUsageService, Session) {
        	
        	//FIXME probably should not be retrieving licenseeId from Session
        	$scope.licenseeId = Session.login;
        	$scope.firstName = Session.firstName;
        	$scope.lastName = Session.lastName;

        	$scope.commercialUsageReports = [];
        	
    		CommercialUsageService.getUsageReports($scope.licenseeId)
			.then(function(result) {
				$scope.commercialUsageReports = [].concat(result);
				$scope.commercialUsageReports.sort(function(a, b) {
					return b.startDate.getTime() - a.endDate.getTime();
				});
				$log.log(result);
			})
			.catch(function(message) {
				//FIXME
				$log.log('Failed to get list of past commercial usage reports');
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
        		var result = usageReport.usage.reduce(function(uniqueCountryCodes, usageReport) {
        			if (uniqueCountryCodes.indexOf(usageReport.countryCode) === -1) {
        				uniqueCountryCodes.push(usageReport.countryCode);
        			}
        			return uniqueCountryCodes;
        		}, []);
        		return result.length;
        	};
        	
        	$scope.openAddUsageReportModal = function() {
        		var modalInstance = $modal.open({
        			templateUrl: 'views/user/addUsageReportModal.html',
        			controller: 'AddUsageReportController',
        			size:'lg',
        			backdrop: 'static',
        			resolve: {
        				licenseeId: function() {
        					return $scope.licenseeId;
        				}
        			}
        		});
        	};

        }
    ]);