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
            .when('/registration', {
              templateUrl: 'templates/Registration.jsp',
              controller: 'UserRegistrationController'
            })
            
            .when('/registration-flow', {
              templateUrl: 'templates/RegistrationFlow.jsp',
              controller: 'UserRegistrationFlowController'
            })
            
            .when('/registration-progress', {
              templateUrl: 'templates/RegistrationApproval.jsp',
              controller: 'UserRegistrationApprovalController'
            })
            
            .when('/', {
                templateUrl: 'templates/LandingPage.html',
                controller: 'UserLoginController'
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