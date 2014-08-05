'use strict';

mldsApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($routeProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $routeProvider
                .when('/countries', {
                    templateUrl: 'views/countries.html',
                    controller: 'CountryController',
                    resolve:{
                        resolvedCountry: ['Countries', function (Countries) {
                            return Countries.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.admin]
                    }
                });
        }]);
