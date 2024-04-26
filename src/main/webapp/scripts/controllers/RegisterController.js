'use strict';

angular.module('MLDS').controller('RegisterController', ['$rootScope', '$scope', '$translate', 'Register', '$location', '$modal', '$log', 'CommercialUsageService','CountryService', 'Session', 'MemberService',
    function ($rootScope, $scope, $translate, Register, $location,$modal, $log, CommercialUsageService, CountryService, Session, MemberService) {
		$scope.availableCountries = CountryService.countries;

        $scope.success = null;
        $scope.error = {};
        $scope.registerAccount = {};
        $scope.confirmPassword = null;

        initFromMemberLanding();

        /**/

        /*MLDS-1000 Extra Questions in Registration Page*/
        $scope.$watch('countryCommonName', function(newValue){
             var country = _.findWhere(CountryService.countries, {'commonName':newValue});
             $scope.registerAccount.country = country;
             if( typeof country !== 'undefined'){
                $scope.countryName = country.commonName;
                $scope.urlRegistration = country.alternateRegistrationUrl;
        if( country.excludeUsage === true){
                    var modalInstance = $modal.open({
                                            template: '<div class="modal-header">'+
                                            '<h4 class="modal-title" translate="register.messages.excludemessage.confirm">Confirmation</h4></div>'+
                                            '<div class="modal-body">{{countryName}} <span translate="register.messages.excludemessage.link">does not use MLDS to distribute SNOMED CT. To register to download SNOMED CT, you can find more information by going to this link -</span> <a href={{urlRegistration}} target="_blank">{{urlRegistration}}</a>.<br><br>'+
                                            '<span translate="register.messages.excludemessage.userConfirm">To continue your registration on MLDS, please can you confirm that you would like to deploy SNOMED CT in a different country.</span></div>'+
                                            '<div class="modal-footer">'+
                                            '<button class="btn btn-danger" ng-click="cancel()" translate="register.messages.excludemessage.cancel">Cancel</button>'+
                                            '<button class="btn btn-primary" ng-click="ok()"><span translate="register.messages.excludemessage.accept">I confirm that I would like to use and deploy SNOMED CT in a country other than</span> {{countryName}}</button>'+
                                            '</div>',
                    controller: function($scope, $modalInstance, countryName, urlRegistration){
                    $scope.countryName = countryName;
                    $scope.urlRegistration = urlRegistration;
                    $scope.ok = function() {
                        $modalInstance.close(true);
                    };
                    $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                    $location.path('/landing').replace();
                    };
        },
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    countryName: function() {
                           return $scope.countryName;
                },
                    urlRegistration: function() {
                        return $scope.urlRegistration;
                    }
            }
            });
           }
        }
        var excludedCountry = false;
        $scope.createUserForm.country.$setValidity('excluded',!excludedCountry);
        });
         /*MLDS-1000 Extra Questions in Registration Page*/

        $scope.register = function () {
    		if ($scope.createUserForm.$invalid) {
    			$scope.createUserForm.attempted = true;
    			return;
    		}
            $scope.registerAccount.langKey = $translate.use();
            $scope.registerAccount.login = $scope.registerAccount.email;

            var initialPeriod = CommercialUsageService.generateRanges()[0];
            $scope.registerAccount.initialUsagePeriod = {
            		startDate: moment(initialPeriod.startDate).format('YYYY-MM-DD'),
            		endDate: moment(initialPeriod.endDate).format('YYYY-MM-DD')
            	};

            CommercialUsageService.generateRanges()[0];
            Register.save($scope.registerAccount,
                function (value, responseHeaders) {
                    $scope.error = {};
                    $scope.success = 'OK';
                    $location.path('/emailVerification');
                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.status === 304) {
                        $scope.error = {userExists: 'ERROR'};
                    } else if (httpResponse.status === 406) {
                    	$scope.error = {onBlocklist: 'ERROR'};
                    } else {
                    	$scope.error = {general: 'ERROR'};
                    }
                });
        };

        function initFromMemberLanding() {
        	if ($rootScope.memberLanding && $rootScope.memberLanding.key) {
        		CountryService.ready.then(function() {
        			var country = CountryService.countriesByIsoCode2[$rootScope.memberLanding.key];
        			$scope.countryCommonName = country.commonName;
        		});
        	}
        }
    }]);
