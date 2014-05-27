'use strict';

angular.module('MLDS', ['ngCookies', 'ngResource', 'ngRoute', 'pascalprecht.translate'])
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
            
            .when('/', {
                templateUrl: 'templates/Login.jsp',
                controller: 'UserLoginController'
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
