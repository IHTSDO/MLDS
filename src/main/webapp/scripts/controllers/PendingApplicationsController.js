'use strict';

angular.module('MLDS').controller('PendingApplicationsController', [
    '$scope',
    '$log',
    '$location',
    '$parse',
    'Session',
    'UserRegistrationService',
    'DomainBlacklistService',
    'PackagesService',
    'AffiliateService',
    'SessionStateService',
    function($scope, $log, $location, $parse, Session, UserRegistrationService, DomainBlacklistService,
        PackagesService, AffiliateService, SessionStateService) {

        $scope.applications = [];
        $scope.isAdmin = Session.isAdmin();
        $scope.homeMember = Session.member || { member: 'NONE' };

        $scope.downloadingApplications = false;
        $scope.page = 0; // Current page (starts from 0)
        $scope.pageSize = 50; // Number of applications per page
        $scope.hasMoreData = true; // Flag to indicate if more data is available
        $scope.noResults = false;

        $scope.generatingCsv = false;

        loadVisualState();


        function loadVisualState() {
            var store = SessionStateService.sessionState.pendingApplicationsFilter;

            $scope.showAllApplications = store.showAllApplications ? store.showAllApplications : 0;
            $scope.orderByField = store.orderByField ? store.orderByField : 'submittedAt';
            $scope.reverseSort = store.reverseSort ? store.reverseSort : false;
        }

        function saveVisualState() {
            var store = SessionStateService.sessionState.pendingApplicationsFilter;

            store.showAllApplications = $scope.showAllApplications;
            store.orderByField = $scope.orderByField;
            store.reverseSort = $scope.reverseSort;
        }

        function loadApplications() {
            // Force clear when reloading the data
            $scope.applications = [];
            $scope.page = 0;
            $scope.hasMoreData = true;
            $scope.noResults = true;
            $scope.canSort = !$scope.query;

            loadMoreApplications();
        }

        function loadMoreApplications() {
            saveVisualState();
            if ($scope.downloadingApplications || !$scope.hasMoreData) {
                return;
            }
            $scope.downloadingApplications = true;
            $scope.alerts = [];

            UserRegistrationService.filterPendingApplications($scope.query, $scope.page, $scope.pageSize, $scope.showAllApplications == 1 ? null : $scope.homeMember, $scope.orderByField, $scope.reverseSort)
                .then(function(response) {
                    $scope.applications = $scope.applications.concat(response.data); // Append new data
                    $scope.page++;
                    $scope.hasMoreData = response.data.length === $scope.pageSize;
                    $scope.noResults = _.size($scope.applications) === 0; // Check for empty results
                    $scope.downloadingApplications = false;

                    if ($scope.loadReset) {
                        loadApplications(); // Reset if needed
                    }
                })
                .catch(function(message) {
                    $scope.downloadingApplications = false;
                    $log.log("affiliates download failure: " + message);
                    $scope.alerts.push({ type: 'danger', msg: 'Network request failure [23]: please try again later.' });
                    if ($scope.loadReset) {
                        loadApplications(); // Reset if needed
                    }
                });
        }

        loadApplications();

        $scope.$watch('showAllApplications', loadApplications);

        $scope.nextPage = function() {
            if ($scope.hasMoreData) {
                loadMoreApplications();
            }
        };

        $scope.toggleField = function(fieldName) {
            if ($scope.orderByField !== fieldName) {
                $scope.reverseSort = false;
            } else {
                $scope.reverseSort = !$scope.reverseSort;
            }
            $scope.orderByField = fieldName;
            loadApplications();
        };


        $scope.goToApplication = function(application) {
            $log.log('application', application);
            $location.path('/applicationReview/' + encodeURIComponent(application.applicationId));
        };

        $scope.totalSublicenses = function(usage) {
            var count = 0;
            if (usage.entries) {
                count += usage.entries.length;
            }
            if (usage.countries) {
                count += usage.countries.reduce(function(total, c) {
                    return total + (c.snomedPractices || 0);
                }, 0);
            }
            return count;
        };

        $scope.generateCsv = function() {
            $scope.generatingCsv = true;
            return UserRegistrationService.filterPendingApplications($scope.query, 0, 999999999, $scope.showAllApplications == 1 ? null : $scope.homeMember, $scope.orderByField, $scope.reverseSort)
                .then(function(response) {
                    var expressions = [
                        $parse("application.applicationId"),
                        $parse("application.affiliateDetails.firstName + ' '+application.affiliateDetails.lastName"),
                        $parse("application.applicationType | enum:'application.applicationType.'"),
                        $parse("isPrimary ? ((application.affiliateDetails.agreementType | enum:'affiliate.agreementType.')||'') : ((application.affiliate.affiliateDetails.agreementType | enum:'affiliate.agreementType.')||'')"),
                        $parse("isPrimary ? (((application.type | enum:'affiliate.type.')||'') + ' - '+ ((application.subType | enum:'affiliate.subType.')||'')) : ((application.affiliate.type | enum:'affiliate.type.')||'')"),
                        $parse("application.submittedAt | date: 'yyyy-MM-dd'"),
                        $parse("application.approvalState | enum:'approval.state.'"),
                        $parse("(application.affiliateDetails.address.country.commonName)||''"),
                        $parse("(application.member.key | enum:'global.member.')||''"),
                        $parse("application.affiliateDetails.email")
                    ];
                    var result = [];
                    _.each(response.data, function(application) {
                        var row = [];
                        _.each(expressions, function(expression) {
                            row.push(expression({
                                'application': application,
                                'isPrimary': application.applicationType === 'PRIMARY',
                                'isExtension': application.applicationType === 'EXTENSION'
                            }));
                        });
                        result.push(row);
                    });
                    $scope.generatingCsv = false;
                    return result;
                })["catch"](function(message) {
                    $scope.generatingCsv = false;
                    $log.log("csv generation failure: " + message);
                    $scope.alerts.push({ type: 'danger', msg: 'Network request failure [48]: please try again later.' });
                });
        };

    }
]);
