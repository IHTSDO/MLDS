'use strict';

describe('Services Tests ', function () {

	var mockLandingRedirectService = {};
	
    beforeEach(module('MLDS', function ($provide){
    	$provide.value('LandingRedirectService', mockLandingRedirectService);
    }));

    describe('AuthenticationSharedService', function () {
        var serviceTested,
            httpBackend,
            authServiceSpied;

        beforeEach(
			inject(function($httpBackend, AuthenticationSharedService, authService, LandingRedirectService) {
	            serviceTested = AuthenticationSharedService;
	            httpBackend = $httpBackend;
	            authServiceSpied = authService;
	            //Request on app init
	            httpBackend.expectGET('i18n/en.json').respond(200, '');
	        })
        );
        
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            httpBackend.verifyNoOutstandingExpectation();
            httpBackend.verifyNoOutstandingRequest();
        });

        it('should call backend on logout then call authService.loginCancelled', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('app/logout').respond(returnData);        

            //Set spy
            spyOn(authServiceSpied, 'loginCancelled');

             //WHEN
            serviceTested.logout();
            //flush the backend to "execute" the request to do the expectedGET assertion.
            httpBackend.flush();

            //THEN
            expect(authServiceSpied.loginCancelled).toHaveBeenCalled();
        });

    });


    describe('AuditsService', function () {
        var serviceTested,
            httpBackend;
        
        beforeEach(inject(function($httpBackend, AuditsService, authService, LandingRedirectService) {
            serviceTested = AuditsService;
            httpBackend = $httpBackend;
            //Request on app init
            httpBackend.expectGET('i18n/en.json').respond(200, '');
        }));
        //make sure no expectations were missed in your tests.
        //(e.g. expectGET or expectPOST)
        afterEach(function() {
            httpBackend.verifyNoOutstandingExpectation();
            httpBackend.verifyNoOutstandingRequest();
        });

        it('should call backend on findAll', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('/app/rest/audits').respond(returnData);        

             //WHEN
            serviceTested.findAll();
            //flush the backend to "execute" the request to do the expectedGET assertion.
            httpBackend.flush();
        });

        it('should call backend on findByDates with filter', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('/app/rest/audits?$filter=auditEventDate%20ge%20\'2014-01-01\'%20and%20auditEventDate%20le%20\'2014-01-09\'').respond(returnData);        

             //WHEN
            serviceTested.findByDates('2014-01-01', '2014-01-09');
            //flush the backend to "execute" the request to do the expectedGET assertion.
            httpBackend.flush();
        });

        it('should call backend on findByAuditEventType with filter', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('/app/rest/audits?$filter=auditEventType%20eq%20\'TestType\'').respond(returnData);        

             //WHEN
            serviceTested.findByAuditEventType('TestType');
            //flush the backend to "execute" the request to do the expectedGET assertion.
            httpBackend.flush();
        });

        it('should call backend on findByAffiliateId with filter', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('/app/rest/audits?$filter=affiliateId%20eq%20\'123\'').respond(returnData);        

             //WHEN
            serviceTested.findByAffiliateId(123);
            //flush the backend to "execute" the request to do the expectedGET assertion.
            httpBackend.flush();
        });

        it('should call backend on findByApplicationId with filter', function(){
            //GIVEN
            //set up some data for the http call to return and test later.
            var returnData = { result: 'ok' };
            //expectGET to make sure this is called once.
            httpBackend.expectGET('/app/rest/audits?$filter=applicationId%20eq%20\'123\'').respond(returnData);        

             //WHEN
            serviceTested.findByApplicationId(123);
            //flush the backend to "execute" the request to do the expectedGET assertion.
            httpBackend.flush();
        });

    });
});


