'use strict';

angular.module('MLDS', ['ngCookies', 'ngResource', 'ngRoute', 'pascalprecht.translate']).config(
        ['$routeProvider', function ($routeProvider) {
          $routeProvider
            .when('/', {
              templateUrl: 'partials/main.html',
              controller: 'MainCtrl'
            })
            /* FIXME AG: find a way to not expose this route in production */
            .when('/e2etest/:action', {
              template: '<div id="container"></div>',
              controller: 'E2ETestCtrl'
            })

            .when('/about', {
              templateUrl: 'views/about.html'
            })
            .when('/contact', {
              templateUrl: 'views/contact.html'
            })
            .when('/users', {
            	templateUrl: 'partials/user-list.html',
            	controller: 'UserController'
            })
            .when('/admin/selfTest', {
            	templateUrl: 'partials/self-test.html',
            	controller: 'SelfTestController'
            })
            .when('/admin/config', {
            	templateUrl: 'partials/configuration.html',
            	controller: 'ConfigurationController'
            })
            .when('/admin/log', {
            	templateUrl: 'partials/event-log.html',
            	controller: 'EventLogController',
            	resolve: {
            		initData: ['eventService', function(eventService){
            			return eventService.list(0, 5);
            		}]
            	}
            })
            .when('/users/new', {
            	templateUrl: 'partials/user-detail-new.html',
            	controller: 'UserDetailController'
            })
            .when('/users/:userId', {
            	templateUrl: 'partials/user-detail.html',
            	controller: 'UserDetailController'
            })
            .when('/dashboard', {
              templateUrl: 'views/dashboard.html',
              controller: 'CompanyListCtrl',
              resolve: {
                listData: ['companies', function(companies) {
                  return companies.list(0, 6);
                }]
              }
            })
            .when('/view-company/:companyId', {
              templateUrl: 'views/view-company.html',
              controller: 'ViewCompanyCtrl',
              resolve: {
                companyDetail: ['company', function(company) {
                  return company.detail();
                }]
              }
            })
            .otherwise({
              redirectTo: '/'
            });
        }]
    ).config(
      ['$httpProvider', function ($httpProvider) {

        delete $httpProvider.defaults.headers.common['X-Requested-With'];

      }]
    );
