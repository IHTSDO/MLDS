'use strict';

mldsApp.factory('AuthenticationSharedService', ['$rootScope', '$http', 'authService', 'Session', 'Account',
    function ($rootScope, $http, authService, Session, Account) {
		var extractErrorCodeFromMessage = function extractErrorCodeFromMessage(message) {
            var errorCodePatternResult = /MLDS_ERR_[A-Z_]+/.exec(message);
            if (errorCodePatternResult) {
            	return errorCodePatternResult[0];
            } else {
            	return null;
            }
		};
        return {
            login: function (param) {
                var data = $.param({ 
                	j_username : param.username, 
                	j_password : param.password,
                	_spring_security_remember_me : param.rememberMe
            	});
                
                $http.post('app/authentication', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    Account.get(function(data) {
                        Session.create(data.login, data.firstName, data.lastName, data.email, data.roles, data.member);
                        $rootScope.account = Session;
                        authService.loginConfirmed(data);
                    });
                }).error(function (data, status, headers, config) {
                    var errorCode = extractErrorCodeFromMessage(data.message);
                    var messageKey;
                    if (errorCode) {
                    	messageKey = {
                    		'MLDS_ERR_AUTH_NO_PERMISSIONS' : 'login.messages.error.noPermissions',
                    		'MLDS_ERR_AUTH_BAD_PASSWORD' : 'login.messages.error.authentication',
                    		'MLDS_ERR_AUTH_SYSTEM' : 'global.messages.error.server'
                    	}[errorCode];
                    }
                    $rootScope.authenticationError = true;
                    $rootScope.authenticationErrorMessageKey = messageKey?messageKey:'login.messages.error.authentication';
                    
                    Session.invalidate();
                });
            },
            valid: function (authorizedRoles) {

                $http.get('protected/transparent.gif', {
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    if (!Session.login) {
                        Account.get(function(data) {
                            Session.create(data.login, data.firstName, data.lastName, data.email, data.roles, data.member);
                            $rootScope.account = Session;

                            if (!$rootScope.isAuthorized(authorizedRoles)) {
                                event.preventDefault();
                                // user is not allowed
                                $rootScope.$broadcast("event:auth-notAuthorized");
                            }

                            $rootScope.authenticated = true;
                        });
                    }
                    $rootScope.authenticated = !!Session.login;
                }).error(function (data, status, headers, config) {
                    $rootScope.authenticated = false;
                    Session.invalidate();
                });
            },
            isAuthorized: function (authorizedRoles) {
                if (!angular.isArray(authorizedRoles)) {
                    if (authorizedRoles == '*') {
                        return true;
                    }

                    authorizedRoles = [authorizedRoles];
                }

                var isAuthorized = false;
                angular.forEach(authorizedRoles, function(authorizedRole) {
                    var authorized = (!!Session.login &&
                        Session.userRoles.indexOf(authorizedRole) !== -1);

                    if (authorized || authorizedRole == '*') {
                        isAuthorized = true;
                    }
                });

                return isAuthorized;
            },
            logout: function () {
                $rootScope.authenticationError = false;
                $rootScope.authenticated = false;
                $rootScope.account = null;

                // logout client-side
                Session.invalidate();
                // logout server-side
                $http.get('app/logout').then(function(){
                    authService.loginCancelled();
                });
            }
        };
    }]);