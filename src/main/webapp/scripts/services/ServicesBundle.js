angular.module('MLDS').factory('ServicesBundle', [
    'PackagesService', 'PackageUtilsService','UserAffiliateService', 'ApplicationUtilsService', 'MemberService', 'StandingStateUtils','ReleasePackageService',
    function( PackagesService, PackageUtilsService, UserAffiliateService, ApplicationUtilsService, MemberService, StandingStateUtils, ReleasePackageService) {
        return {
            PackagesService: PackagesService,
            PackageUtilsService: PackageUtilsService,
            UserAffiliateService: UserAffiliateService,
            ApplicationUtilsService: ApplicationUtilsService,
            MemberService: MemberService,
            StandingStateUtils: StandingStateUtils,
            ReleasePackageService: ReleasePackageService,
        };
    }
]);
