'use strict';

angular.module('MLDS').controller('MemberLandingPageController', 
		['$scope', '$rootScope', 'Session', '$log', '$routeParams', 'MemberService', '$location', '$translate', 'CountryService',
		function ($scope, $rootScope, Session, $log, $routeParams, MemberService, $location, $translate, CountryService) {
			var memberKey = $routeParams.memberKey;
			var langISO = $location.search()['lang'];
			console.log(memberKey + " with language " + langISO);
			$translate.use(langISO);

			MemberService.ready.then(function() {
				var member = MemberService.membersByKey[memberKey];
				// Introduce a new memberLanding variable rather than reuse Session.member as no
				// authority is conferred by hitting this public page
				$rootScope.memberLanding = member;
			});

			var setLandingText = function () {
				//If we have a memberKey then set a country specific landing message.   
				//otherwise we'll use the standard IHTSDO text
				if (memberKey == null){
					$scope.landingText = $translate.instant('views.landingPage.purpose');
				} else {
					CountryService.ready.then(function() {
						var country = CountryService.countriesByIsoCode2[memberKey];
						var countryCommonName = country.commonName;
						$scope.landingText = 	$translate.instant('views.landingPage.member.purpose1') +
												$translate.instant('global.country.'+ $rootScope.memberLanding.key) +
												$translate.instant('views.landingPage.member.purpose2') +
												$translate.instant('global.country.'+ $rootScope.memberLanding.key) +
												$translate.instant('views.landingPage.member.purpose3');
					});
				}
			}
			
			$rootScope.$on('$translateChangeSuccess', function () {
				setLandingText();
			});
			
			setLandingText();
	}]);