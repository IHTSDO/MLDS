'use strict';

angular.module('MLDS').controller('AdminUsageReportsController',
		['$scope', '$log', 'CommercialUsageService', 'UsageReportsService', 'StandingStateUtils', 'Session',
    function ($scope, $log, CommercialUsageService,UsageReportsService, StandingStateUtils, Session) {

			$scope.usageReportsUtils = UsageReportsService;
			$scope.isAdmin = Session.isAdmin();

			$scope.usageReports = [];

			$scope.orderByField = 'submitted';
			$scope.reverseSort = false;

			var memberKey = Session.member && Session.member.key || 'NONE';
			$scope.reportSearch = function() {
				return (function(report) {
					if ($scope.isAdmin){
						return true;
					}
					return report.affiliate.homeMember.key === memberKey;
				});
			};

			function loadUsageReports(){
				CommercialUsageService.getSubmittedUsageReports()
					.then(function(results) {
						$scope.usageReports = results.data;
					});
			}

			loadUsageReports();

			$scope.affiliateDetails = function(usageReport) {
				if (!usageReport || !usageReport.affiliate) {
					return {};
				}
				var affiliate = usageReport.affiliate;
				if (StandingStateUtils.wasApproved(affiliate.standingState)) {
					return affiliate.affiliateDetails;
				} else {
					//TODO would like to find application when not included
					return affiliate.application && affiliate.application.affiliateDetails || {};
				}
			};
			/*MLDS-985 Review Usage Reports*/
            $scope.generateCsv = function() {
            $scope.generatingCsv = true;
            /*console.log(Session.member.key);*/
            return UsageReportsService.getReviewUsageReport()
                .then(function(response) {
            var data = response.data;
            var csvString = "Affiliate Id,Member Key,Deployment Country,Affiliate Country,Start Date,End Date,Standing State,Created,Agreement Type,Applicant,Type,Organization Name,Organization Type,Current Usage,Planned Usage,Purpose,Implementation Status,Other Activities,Snomed Practices,Hospital Staffing Practice,Databases Per Deployment,Deployed Data Analysis Systems,Hospitals\n"; // CSV header row
            data.forEach(function(item) {
            var shouldInclude = Session.member.key === "IHTSDO" || item[2] === Session.member.key || item[3] === Session.member.key;
            if (shouldInclude) {
            var row = [item[0],item[1],item[2],item[3],item[4],item[5],item[6],item[7],item[8],item[9],item[10],
             item[11],item[12],item[13],item[14],item[15],item[16],item[17],item[18],item[19],item[20],item[21],item[22]
            ];
            csvString += row.join(",") + "\n";
             }
            });
             var blob = new Blob([csvString], { type: "text/csv;charset=utf-8;" });
             var downloadLink = document.createElement("a");
             downloadLink.setAttribute("href", URL.createObjectURL(blob));
             downloadLink.setAttribute("download", "usage-report.csv");
             document.body.appendChild(downloadLink);
             downloadLink.click();
             $scope.generatingCsv = false;
             });
            }
			/*MLDS-985 Review Usage Reports*/
    }]);

