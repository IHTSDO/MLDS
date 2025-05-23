'use strict';

/* App Module */

let mldsApp = angular.module('MLDS', ['http-auth-interceptor', 'tmh.dynamicLocale',
    'ngResource', 'ngRoute', 'ngCookies', 'ngSanitize', 'mldsAppUtils', 'pascalprecht.translate', 'truncate',  'ui.bootstrap', 'infinite-scroll', 'ngCsv', 'textAngular']);
mldsApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider',  'tmhDynamicLocaleProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider, USER_ROLES) {
            $routeProvider
                .when('/register', {
                    templateUrl: 'views/register.html',
                    controller: 'RegisterController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/emailVerification', {
                    templateUrl: 'views/registration/emailVerification.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/affiliateRegistration', {
                    templateUrl: 'views/registration/affiliateRegistration.html',
                    controller: 'AffiliateRegistrationController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/activate', {
                    templateUrl: 'views/activate.html',
                    controller: 'ActivationController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/dashboard', {
                    templateUrl: 'views/user/userDashboard.html',
                    controller: 'UserDashboardController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}],
                    	UserAffiliateServiceLoaded:['UserAffiliateService', function(UserAffiliateService){ return UserAffiliateService.promise;}]
                    }
                })
                .when('/usageReports', {
                    templateUrl: 'views/user/usageReports.html',
                    controller: 'UsageReportsController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/usageReportsReview', {
                    templateUrl: 'views/admin/usageReportsReview.html',
                    controller: 'AdminUsageReportsController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/usageReportsReview/:usageReportId', {
                    templateUrl: 'views/admin/usageReportReview.html',
                    controller: 'UsageReportReviewController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/usageReports/usageLog/:usageReportId', {
                    templateUrl: 'views/user/fullPageUsageLog.html',
                    controller: 'FullPageUsageLogController',
                    access: {
                        authorizedRoles: [USER_ROLES.user]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'LoginController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/error', {
                    templateUrl: 'views/error.html',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/contactInfo', {
                    templateUrl: 'views/contactInfo.html',
                    controller: 'ContactInfoController',
                    access: {
                    	authorizedRoles: [USER_ROLES.user]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/password', {
                    templateUrl: 'views/password.html',
                    controller: 'PasswordController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/requestPasswordReset', {
                	templateUrl: 'views/requestPasswordReset.html',
                	controller: 'RequestPasswordResetController',
                	access: {
                		authorizedRoles: [USER_ROLES.all]
                	},
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
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
                        }],
                        lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/releaseManagement', {
                    templateUrl: 'views/admin/releaseManagement.html',
                    controller: 'ReleaseManagementController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/archiveReleases', {
                    templateUrl: 'views/admin/archiveRelease.html',
                    controller: 'ArchiveManagementController',
                    access: {
                       authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
               .when('/releaseManagement/release/:packageId', {
                    templateUrl: 'views/admin/release.html',
                    controller: 'ReleaseController',
                    access: {
                    	authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/archiveReleases/archivePackage/:packageId', {
                    templateUrl: 'views/admin/archiveVersions.html',
                    controller: 'ReleaseController',
                    access: {
                       authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                       lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/releaseManagement/release/:packageId/:edit', {
                    templateUrl: 'views/admin/release.html',
                    controller: 'ReleaseController',
                    access: {
                    	authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/viewReleases', {
                    templateUrl: 'views/user/viewReleases.html',
                    controller: 'ViewReleasesController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}],
                    	membersLoaded:['MemberService', function(MemberService){return MemberService.ready;}],
                    	releasePackagesQueryResult: ['PackagesService', function(PackagesService){return PackagesService.query().$promise;}]
                    }
                })
                .when('/viewReleases/viewRelease/:releasePackageId', {
                    templateUrl: 'views/user/viewRelease.html',
                    controller: 'ViewReleaseController',
                    access: {
                    	authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/extensionApplication/:applicationId', {
                    templateUrl: 'views/user/extensionApplication.html',
                    controller: 'ExtensionApplicationController',
                    access: {
                    	authorizedRoles: [USER_ROLES.user]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}],
                    	application:['$route','UserRegistrationService', function($route, UserRegistrationService) {
                    		return UserRegistrationService.getApplicationById($route.current.params.applicationId).then(function(resp){return resp.data});
                		}]
                    }
                })
                .when('/pendingApplications', {
                    templateUrl: 'views/admin/pendingApplications.html',
                    controller: 'PendingApplicationsController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/applicationReview/:applicationId', {
                    templateUrl: 'views/admin/applicationReview.html',
                    controller: 'ApplicationReviewController',
                    access: {
                    	authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/affiliateManagement', {
                    templateUrl: 'views/admin/affiliateManagement.html',
                    controller: 'AffiliateManagementController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/affiliateManagement/:affiliateId', {
                    templateUrl: 'views/admin/affiliateSummary.html',
                    controller: 'AffiliateSummaryController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/affiliateManagement/:affiliateId/edit', {
                    templateUrl: 'views/admin/editAffiliate.html',
                    controller: 'EditAffiliateController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/memberManagement', {
                    templateUrl: 'views/admin/memberManagement.html',
                    controller: 'MemberManagementController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/memberManagement/:memberKey/branding', {
                    templateUrl: 'views/admin/showMemberBranding.html',
                    controller: 'ShowMemberBrandingController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/postAnnouncement', {
                    templateUrl: 'views/admin/postAnnouncement.html',
                    controller: 'PostAnnouncementController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/ihtsdoReleases', {
                    templateUrl: 'views/admin/ihtsdoReleases.html',
                    controller: 'IHTSDOReleasesController',
                    access: {
                    	authorizedRoles: USER_ROLES.memberOrStaffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}],
                    	membersLoaded:['MemberService', function(MemberService){return MemberService.ready;}],
                    	releasePackagesQueryResult: ['PackagesService', function(PackagesService){return PackagesService.query().$promise;}]
                    }
                })
                .when('/ihtsdoReleases/ihtsdoRelease/:releasePackageId', {
                    templateUrl: 'views/admin/ihtsdoRelease.html',
                    controller: 'IHTSDOReleaseController',
                    access: {
                    	authorizedRoles: USER_ROLES.memberOrStaffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })

                .when('/metrics', {
                    templateUrl: 'views/admin/metrics.html',
                    controller: 'MetricsController',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/logs', {
                    templateUrl: 'views/admin/logs.html',
                    controller: 'LogsController',
                    resolve:{
                        resolvedLogs:['LogsService', function (LogsService) {
                            return LogsService.findAll();
                        }],
                        lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
                .when('/activityLog', {
                    templateUrl: 'views/admin/activityLogs.html',
                    controller: 'ActivityLogsController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                 .when('/fileDownloadReport', {
                                                    templateUrl: 'views/admin/releaseFileDownloadCount.html',
                                                    controller: 'ReleaseFileDownloadCountController',
                                                    access: {
                                                        authorizedRoles: [USER_ROLES.admin]
                                                    },
                                                    resolve: {
                                                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                                                    }
                                                })
                .when('/logout', {
                    templateUrl: 'views/logout.html',
                    controller: 'LogoutController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/docs', {
                    templateUrl: 'views/admin/docs.html',
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/styleguide/:template*', {
                    templateUrl: function(params){
                    	return 'views/styleguide/'+params.template+'.html';},
                    access: {
                        authorizedRoles: USER_ROLES.staffOrAdmin
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/blocklist', {
                    templateUrl: 'views/admin/blocklist.html',
                    controller: 'BlocklistController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/importAffiliates', {
                    templateUrl: 'views/admin/importAffiliates.html',
                    controller: 'ImportAffiliatesController',
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    },
                    resolve: {
                    	lookupsLoaded:['LookupCollector', function(LookupCollector){return LookupCollector.promise;}]
                    }
                })
                .when('/landing', {
                    templateUrl: 'views/landingPage.html',
                    controller: 'MemberLandingPageController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/landing/:memberKey', {
                    templateUrl: 'views/landingPage.html',
                    controller: 'MemberLandingPageController',
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .when('/', {
                	templateUrl: 'views/loading.html',
                	controller: 'LandingRedirectController',
                	access: {
                		authorizedRoles: [USER_ROLES.all]
                	}
                })
                .otherwise({
                	templateUrl: 'views/loading.html',
                    controller: 'LandingRedirectController',
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

                let apiDelay = 2000;
                let otherDelay = 1000;
                let errorMethodMatches = /xPUT|xDELETE|xPOST/;

                return function(promise) {
                    return promise.then(function(response) {
                        return $timeout(function() {
                        	if (errorMethodMatches.test(response.config.method)) {
                        		return $q.reject(response);
                        	} else {
                        		return response;
                        	}
                        }, (response.config.url.indexOf('/rest') !== -1 || response.config.url.indexOf('/api') !== -1   ? apiDelay : otherDelay));
                    }, function(response) {
                        return $q.reject(response);
                    });
                };

        }])
        .run(['datepickerConfig', function(datepickerConfig){
        	// change default date config to ISO8601 and Euro Monday weeks.
        	datepickerConfig.format="yyyy-MM-dd";
        	datepickerConfig.startingDay=1;
        	datepickerConfig.formatYear='yy';
        }])
        .run(['$rootScope', '$location', '$http', 'AuthenticationSharedService', 'Session', 'USER_ROLES', 'LandingRedirectService',
            function($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES, LandingRedirectService) {

                $rootScope.$on('$routeChangeStart', function (event, next) {
                	 $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
                    $rootScope.userRoles = USER_ROLES;
                    if (next.access?.authorizedRoles) {
                    	AuthenticationSharedService.valid(next.access.authorizedRoles);
                    }
                });

                // Call when the the client is confirmed
                $rootScope.$on('event:auth-loginConfirmed', function(data) {
                    $rootScope.authenticated = true;
                    if ($location.path() === "/login") {
                    	LandingRedirectService.redirect(Session);
                    	//$location.path('/'); // redirect landing page
                    }
                });

                // Call when the 401 response is returned by the server
                $rootScope.$on('event:auth-loginRequired', function(rejection) {
                    Session.invalidate();
                    $rootScope.authenticated = false;

                    if ($location.path() !== "/" &&
                    		$location.path() !== "" &&
                    		$location.path() !== "/landing" &&
                    		$location.path() !== "/register" &&
                    		$location.path() !== "/requestPasswordReset" &&
                    		$location.path() !== "/resetPassword" &&
                    		$location.path().indexOf("/viewRelease") == -1 &&
                    		$location.path() !== "/viewReleases" &&
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
                    $location.path('/login');
                });
        }]);
