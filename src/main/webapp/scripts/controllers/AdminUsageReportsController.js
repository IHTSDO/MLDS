'use strict';

angular.module('MLDS').controller('AdminUsageReportsController', ['$scope', '$log', 'CommercialUsageService', 'UsageReportsService', 'StandingStateUtils', 'Session',
    function ($scope, $log, CommercialUsageService, UsageReportsService, StandingStateUtils, Session) {

        $scope.usageReportsUtils = UsageReportsService;
        $scope.isAdmin = Session.isAdmin();

        $scope.usageReports = [];
        $scope.orderByField = 'submitted';
        $scope.reverseSort = false;
        $scope.page = 0;
        $scope.pageSize = 20;
        $scope.hasMoreData = true;
        $scope.downloadingReports = false;

        let memberKey = Session?.member?.key || 'NONE';
        $scope.reportSearch = function() {
            return function(report) {
                if ($scope.isAdmin) {
                    return true;
                }
                return report.affiliate.homeMember.key === memberKey;
            };
        };

        $scope.loadMoreUsageReports = function(reset) {
            if ($scope.downloadingReports || !$scope.hasMoreData) {
                return;
            }
            $scope.downloadingReports = true;
            if (reset) {
                $scope.page = 0;
                $scope.usageReports = [];
                $scope.hasMoreData = true;
            }
            let orderByParam = $scope.orderByField + ',' + ($scope.reverseSort ? 'desc' : 'asc');
            CommercialUsageService.getSubmittedUsageReports($scope.page, $scope.pageSize, orderByParam)
                .then(function(results) {
                    if (results.data.length < $scope.pageSize) {
                        $scope.hasMoreData = false;
                    }
                    $scope.usageReports = $scope.usageReports.concat(results.data);
                    $scope.page++;
                })
                .finally(function() {
                    $scope.downloadingReports = false;
                });
        };

        $scope.toggleField = function(field) {
            if ($scope.orderByField === field) {
                $scope.reverseSort = !$scope.reverseSort;
            } else {
                $scope.orderByField = field;
                $scope.reverseSort = false;
            }
            $scope.loadMoreUsageReports(true);
        };


        $scope.affiliateDetails = function(usageReport) {
            if (!usageReport?.affiliate) {
                return {};
            }
            let affiliate = usageReport.affiliate;
            if (StandingStateUtils.wasApproved(affiliate.standingState)) {
                return affiliate.affiliateDetails;
            } else {
                return affiliate?.application?.affiliateDetails || {};
            }
        };			/*MLDS-985 Review Usage Reports*/
            $scope.generateCsv = function() {
                $scope.generatingCsv = true;
                return UsageReportsService.getReviewUsageReport()
                    .then(processResponse)
                    .then(createCsvString)
                    .then(downloadCsv)
                    .finally(() => {
                        $scope.generatingCsv = false;
                    });
            };

            function processResponse(response) {
                return response.data;
            }

            function createCsvString(data) {
                const csvHeader = [
                    "Affiliate Id", "Member Key", "Deployment Country", "Affiliate Country", "Start Date",
                    "End Date", "Standing State", "Created", "Agreement Type", "Applicant", "Type",
                    "Organization Name", "Organization Type", "Current Usage", "Planned Usage", "Purpose",
                    "Implementation Status", "Other Activities", "Snomed Practices", "Hospital Staffing Practice",
                    "Databases Per Deployment", "Deployed Data Analysis Systems", "Hospitals"
                ].map(field => `"${field}"`).join(",") + "\n";

                const csvRows = data.filter(shouldIncludeItem).map(formatItem).join("\n");
                return csvHeader + csvRows;
            }

            function shouldIncludeItem(item) {
                return Session.member.key === "IHTSDO" || item[2] === Session.member.key || item[3] === Session.member.key;
            }

            function formatItem(item) {
                return [
                    item[0], item[1], item[2], item[3], item[4], item[5], item[6], item[7], item[8], item[9], item[10],
                    item[11], item[12], item[13], item[14], item[15], item[16], item[17], item[18], item[19], item[20], item[21], item[22]
                ].map(field => `"${String(field).replace(/"/g, '""')}"`).join(",");
            }

            function downloadCsv(csvString) {
                const blob = new Blob([csvString], { type: "text/csv;charset=utf-8;" });
                const downloadLink = document.createElement("a");
                downloadLink.setAttribute("href", URL.createObjectURL(blob));
                downloadLink.setAttribute("download", "usage-report.csv");
                document.body.appendChild(downloadLink);
                downloadLink.click();
                document.body.removeChild(downloadLink); // Remove the link after clicking
            }
			/*MLDS-985 Review Usage Reports*/
    }]);

