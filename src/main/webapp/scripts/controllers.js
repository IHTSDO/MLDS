'use strict';

/* Controllers */

mldsApp.controller('AdminController', ['$scope',
    function ($scope) {
    }]);

mldsApp.controller('LanguageController', ['$scope', '$translate', 'CountryService',
    function ($scope, $translate, CountryService) {
        $scope.changeLanguage = function (languageKey) {
        /*MLDS-957- Sweden Language issue*/
            window.setTimeout('window.location.reload()',200);
        /*MLDS-957- Sweden Language issue*/
            $translate.use(languageKey);
        };
    }]);

mldsApp.controller('MenuController', ['$scope',
    function ($scope) {
    }]);


mldsApp.controller('PasswordController', ['$scope', 'Password',
    function ($scope, Password) {
        $scope.success = null;
        $scope.error = null;
        $scope.changePassword = function () {
    		if ($scope.form.$invalid) {
    			$scope.form.attempted = true;
    			return;
    		}
            Password.save($scope.password,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

mldsApp.controller('SessionsController', ['$scope', 'resolvedSessions', 'Sessions',
    function ($scope, resolvedSessions, Sessions) {
        $scope.success = null;
        $scope.error = null;
        $scope.sessions = resolvedSessions;

        $scope.invalidate = function (series) {
            Sessions["delete"]({series: encodeURIComponent(series)},
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = "OK";
                    $scope.sessions = Sessions.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

 mldsApp.controller('MetricsController', ['$scope', 'MetricsService', 'HealthCheckService', 'ThreadDumpService',
    function ($scope, MetricsService, HealthCheckService, ThreadDumpService) {

        $scope.refresh = function() {
            HealthCheckService.check().then(function(data) {
                $scope.healthCheck = data;
            });

            $scope.metrics = MetricsService.get();

            $scope.metrics.$get({}, function(items) {

                $scope.servicesStats = {};
                $scope.cachesStats = {};
                angular.forEach(items.timers, function(value, key) {
                    if (key.indexOf("web.rest") != -1 || key.indexOf("service") != -1) {
                        $scope.servicesStats[key] = value;
                    }

                    if (key.indexOf("net.sf.ehcache.Cache") != -1) {
                        // remove gets or puts
                        let index = key.lastIndexOf(".");
                        let newKey = key.substr(0, index);

                        // Keep the name of the domain
                        index = newKey.lastIndexOf(".");
                        $scope.cachesStats[newKey] = {
                            'name': newKey.substr(index + 1),
                            'value': value
                        };
                    }
                });
            });
        };

        $scope.refresh();

        $scope.threadDump = function() {
            ThreadDumpService.dump().then(function(data) {
                $scope.threadDump = data;

                $scope.threadDumpRunnable = 0;
                $scope.threadDumpWaiting = 0;
                $scope.threadDumpTimedWaiting = 0;
                $scope.threadDumpBlocked = 0;

                angular.forEach(data, function(value, key) {
                    if (value.threadState == 'RUNNABLE') {
                        $scope.threadDumpRunnable += 1;
                    } else if (value.threadState == 'WAITING') {
                        $scope.threadDumpWaiting += 1;
                    } else if (value.threadState == 'TIMED_WAITING') {
                        $scope.threadDumpTimedWaiting += 1;
                    } else if (value.threadState == 'BLOCKED') {
                        $scope.threadDumpBlocked += 1;
                    }
                });

                $scope.threadDumpAll = $scope.threadDumpRunnable + $scope.threadDumpWaiting +
                    $scope.threadDumpTimedWaiting + $scope.threadDumpBlocked;

            });
        };

        $scope.getLabelClass = function(threadState) {
            if (threadState == 'RUNNABLE') {
                return "label-success";
            } else if (threadState == 'WAITING') {
                return "label-info";
            } else if (threadState == 'TIMED_WAITING') {
                return "label-warning";
            } else if (threadState == 'BLOCKED') {
                return "label-danger";
            }
        };
    }]);

mldsApp.controller('LogsController', ['$scope', 'resolvedLogs', 'LogsService',
    function ($scope, resolvedLogs, LogsService) {
        $scope.loggers = resolvedLogs;

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        };
    }]);

mldsApp.controller('ActivityLogsController', ['$scope', '$translate', '$filter', 'AuditsService',
    function ($scope, $translate, $filter, AuditsService) {
		$scope.submitting = false;

		function loadActivity() {
			$scope.submitting = true;
			AuditsService.findByDates($scope.fromDate, $scope.toDate)
			.then(function(data){
				$scope.audits = data;
				$scope.submitting = false;
			})
			["catch"](function(message) {
				$scope.submitting = false;
			});

		}

		function toDateFilter(m) {
			return $filter('date')(m.toDate(), "yyyy-MM-dd");
		}

        $scope.onChangeDate = loadActivity;

        // Date picker configuration
        $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            $scope.fromDate = toDateFilter(moment());
            $scope.toDate = toDateFilter(moment().add(1, 'days'));
        };

        $scope.previousWeek = function() {
            $scope.fromDate = toDateFilter(moment().subtract(1, 'weeks'));
            $scope.toDate = toDateFilter(moment().add(1, 'days'));
        };

        $scope.previousMonth = function() {
            $scope.fromDate = toDateFilter(moment().subtract(1, 'months'));
            $scope.toDate = toDateFilter(moment().add(1, 'days'));
        };

        $scope.previousWeek();

        loadActivity();
    }]);
    mldsApp.controller('ReleaseFileDownloadCountController', ['$scope', '$http', '$filter', 'ReleaseFileDownloadCountService', function ($scope, $http, $filter, ReleaseFileDownloadCountService) {
        $scope.submitting = false;
        $scope.omitUsers = [];
        $scope.userList = []; // Initially empty

        $scope.loadReleaseFileDownloadCounts = function() {
            $scope.submitting = true;
            let params = {
                startDate: $scope.fromDate,
                endDate: $scope.toDate,
                excludeUsers: $scope.omitUsers
            };

            ReleaseFileDownloadCountService.findReleaseFileDownloadCounts(params)
            .then(function(data) {
                $scope.releaseFileDownloadCounts = data;
                $scope.submitting = false;
            })
            .catch(function(message) {
                $scope.submitting = false;
            });
        };

        function toDateFilter(m) {
            return $filter('date')(m.toDate(), "yyyy-MM-dd");
        }

        // Date picker configuration
        $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            $scope.fromDate = toDateFilter(moment());
            $scope.toDate = toDateFilter(moment().add(1, 'days'));
        };

        $scope.previousWeek = function() {
            $scope.fromDate = toDateFilter(moment().subtract(1, 'weeks'));
            $scope.toDate = toDateFilter(moment().add(1, 'days'));
        };

        $scope.previousMonth = function() {
            $scope.fromDate = toDateFilter(moment().subtract(1, 'months'));
            $scope.toDate = toDateFilter(moment().add(1, 'days'));
        };

        // Function to load user list based on startDate and endDate
        $scope.loadUsers = function() {
        $scope.userList = [];
        $scope.omitUsers = [];
             let params = {
                        startDate: $scope.fromDate,
                        endDate: $scope.toDate,
                    };

            // Adjust the URL to include startDate and endDate as query parameters
            let url = '/api/audits/getAllUsersonSelectedDate?startDate=' + params.startDate + '&endDate=' +params.endDate;

            $http.get(url).then(function(response) {
                $scope.userList = response.data;
            }, function(error) {
                // Handle error
                console.error('Error loading users:', error);
            });
        };

        $scope.toggleSelection = function(user) {
            let index = $scope.omitUsers.indexOf(user);
            if (index === -1) {
                $scope.omitUsers.push(user);
            } else {
                $scope.omitUsers.splice(index, 1);
            }
        };

        // Initial load
        $scope.previousWeek(); // Set initial date range
    }]);

mldsApp.controller('FooterController', ['$scope',
    function ($scope) {
        document.getElementById('copyright').innerHTML = 'Copyright © ' + new Date().getFullYear() + ' SNOMED International';
    }]);
