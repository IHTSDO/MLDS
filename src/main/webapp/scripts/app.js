'use strict';

/* App Module */

var mldsApp = angular.module('MLDS', ['http-auth-interceptor', 'tmh.dynamicLocale',
    'ngResource', 'ngRoute', 'ngCookies', 'mldsAppUtils', 'pascalprecht.translate', 'truncate', 'ui.bootstrap']);

mldsApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider',  'tmhDynamicLocaleProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider, USER_ROLES) {
            $routeProvider
                .when('/register', {
                    templateUrl: 'views/register.html',
                    controller: 'RegisterController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/emailVerification', {
                    templateUrl: 'views/registration/emailVerification.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/affiliateRegistration', {
                    templateUrl: 'views/registration/affiliateRegistration.html',
                    controller: 'AffiliateRegistrationController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/activate', {
                    templateUrl: 'views/activate.html',
                    controller: 'ActivationController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/dashboard', {
                    templateUrl: 'views/user/Dashboard.html',
                    controller: 'UserDashboardController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/usage-log/:usageReportId', {
                    templateUrl: 'views/user/fullPageUsageLog.html',
                    controller: 'FullPageUsageLogController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/error', {
                    templateUrl: 'views/error.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/settings', {
                    templateUrl: 'views/settings.html',
                    controller: 'SettingsController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/password', {
                    templateUrl: 'views/password.html',
                    controller: 'PasswordController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/requestPasswordReset', {
                	templateUrl: 'views/requestPasswordReset.html',
                	controller: 'RequestPasswordResetController',
                	access: {
                		authorizedRoles: [USER_ROLES.all]
                	}
                })
                .when('/resetPassword', {
                	templateUrl: 'views/resetPassword.html',
                	controller: 'ResetPasswordController',
                	access: {
                		authorizedRoles: [USER_ROLES.all]
                	}
                })
                .when('/sessions', {
                    templateUrl: 'views/sessions.html',
                    controller: 'SessionsController',
                    resolve:{
                        resolvedSessions:['Sessions', function (Sessions) {
                            return Sessions.get();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/adminDashboard', {
                    templateUrl: 'views/admin/dashboard.html',
                    controller: 'AdminDashboardController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/packageManagement', {
                    templateUrl: 'views/admin/packageManagement.html',
                    controller: 'PackageManagementController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                // FIXME MLDS-50 MB can we push these routes down to /admin and leave these names for the user?
                .when('/package/:packageId', {
                    templateUrl: 'views/admin/package.html',
                    controller: 'PackageController',
                    access: {
                    	authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/package/:packageId/:edit', {
                    templateUrl: 'views/admin/package.html',
                    controller: 'PackageController',
                    access: {
                    	authorizedRoles: [USER_ROLES.admin]
                    }
                })
                // FIXME MLDS-50 MB can we push these routes down to /admin and leave these names for the user?
                .when('/viewPackages', {
                    templateUrl: 'views/user/viewPackages.html',
                    controller: 'ViewPackagesController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/viewPackage/:packageId', {
                    templateUrl: 'views/user/viewPackage.html',
                    controller: 'ViewPackageController',
                    access: {
                    	authorizedRoles: [USER_ROLES.user]
                    }
                })
                .when('/pendingApplications', {
                    templateUrl: 'views/admin/pendingApplications.html',
                    controller: 'PendingApplicationsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/applicationReview/:applicationId', {
                    templateUrl: 'views/admin/applicationReview.html',
                    controller: 'ApplicationReviewController',
                    access: {
                    	authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/metrics', {
                    templateUrl: 'views/admin/metrics.html',
                    controller: 'MetricsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logs', {
                    templateUrl: 'views/admin/logs.html',
                    controller: 'LogsController',
                    resolve:{
                        resolvedLogs:['LogsService', function (LogsService) {
                            return LogsService.findAll();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/audits', {
                    templateUrl: 'views/admin/audits.html',
                    controller: 'AuditsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/logout', {
                    templateUrl: 'views/main.html',
                    controller: 'LogoutController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/docs', {
                    templateUrl: 'views/admin/docs.html',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .otherwise({
                    templateUrl: 'views/landingPage.html',
                    controller: 'MainController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                });

            // Initialize angular-translate
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });

            $translateProvider.preferredLanguage('en');

            $translateProvider.useCookieStorage();

            tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js')
            tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
            
//            var apiDelay = 1000;
//            var otherDelay = 0;
//            var errorMethodMatches = /PUT|xDELETE|xPOST/;
//            var delayHandlerFactory = function($q, $timeout) {
//                return function(promise) {
//                    return promise.then(function(response) {
//                        return $timeout(function() {
//                        	if (errorMethodMatches.test(response.config.method)) {
//                        		return $q.reject(response);		
//                        	} else {
//                        		return response;
//                        	}
//                        }, (response.config.url.indexOf('/rest') !== -1 || response.config.url.indexOf('/api') !== -1   ? apiDelay : otherDelay));
//                    }, function(response) {
//                        return $q.reject(response);
//                    });
//                };
//            };
//            $httpProvider.responseInterceptors.push(delayHandlerFactory);
            
        }])
        .run(['$rootScope', '$location', '$http', 'AuthenticationSharedService', 'Session', 'USER_ROLES',
            function($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES) {
                $rootScope.$on('$routeChangeStart', function (event, next) {
                    $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
                    $rootScope.userRoles = USER_ROLES;
                    AuthenticationSharedService.valid(next.access.authorizedRoles);
                });

                // Call when the the client is confirmed
                $rootScope.$on('event:auth-loginConfirmed', function(data) {
                    $rootScope.authenticated = true;
                    if ($location.path() === "/login") {
                        $location.path('/dashboard').replace();
                    }
                });

                // Call when the 401 response is returned by the server
                $rootScope.$on('event:auth-loginRequired', function(rejection) {
                    Session.invalidate();
                    $rootScope.authenticated = false;
                    if ($location.path() !== "/" && 
                    		$location.path() !== "" && 
                    		$location.path() !== "/register" &&
                    		// FIXME MB is there a better way to register anonymous pages?
                    		$location.path() !== "/requestPasswordReset" &&
                    		$location.path() !== "/resetPassword" &&
                    		$location.path() !== "/emailVerification" &&
                            $location.path() !== "/activate") {
                        $location.path('/login').replace();
                    }
                });

                // Call when the 403 response is returned by the server
                $rootScope.$on('event:auth-notAuthorized', function(rejection) {
                    $rootScope.errorMessage = 'errors.403';
                    $location.path('/error').replace();
                });

                // Call when the user logs out
                $rootScope.$on('event:auth-loginCancelled', function() {
                    $location.path('');
                });
        }]);
