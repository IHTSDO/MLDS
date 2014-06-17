'use strict';

angular.module('MLDS', ['ngCookies', 'ngResource', 'ngRoute', 'pascalprecht.translate'])
	.config(
      ['$httpProvider', function ($httpProvider) {

        delete $httpProvider.defaults.headers.common['X-Requested-With'];

      }]
    );

angular.module('MLDS-Registration', ['MLDS'])
.config(
    ['$routeProvider', function ($routeProvider) {
          $routeProvider
            .when('/', {
                templateUrl: 'templates/LandingPage.html'
            })
            .when('/emailVerification', {
                templateUrl: 'templates/registration/emailVerification.html'
            })
            .otherwise({
              redirectTo: '/'
            });
        }]
    );

angular.module('MLDS-User', ['MLDS'])
.config(
    ['$routeProvider', function ($routeProvider) {
      $routeProvider
        .when('/', {
            templateUrl: 'templates/User/Dashboard.html',
            controller: 'UserDashboardController'
        })
        .when('/affiliateRegistration', {
          templateUrl: 'templates/registration/affiliateRegistration.html'
          //controller: 'AffiliateRegistrationController'
        })
        
        .when('/pendingRegistration', {
          templateUrl: 'templates/registration/pendingRegistration.html'
        })
        .when('/logout', {
            templateUrl: 'templates/LandingPage.html',
            controller: 'LogoutController'
        })
            
            
        .otherwise({
          redirectTo: '/'
        });
    }]
);

angular.module('MLDS-Admin', ['MLDS'])
.config(
    ['$routeProvider', function ($routeProvider) {
      $routeProvider
        .when('/', {
            templateUrl: 'templates/Admin/Dashboard.html',
            controller: 'AdminDashboardController'
        })
        .otherwise({
          redirectTo: '/'
        });
    }]
);