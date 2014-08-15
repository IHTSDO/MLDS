'use strict';

describe('UserAffiliateService Tests ', function () {

    beforeEach(module('MLDS'));

    describe('initializing affiliate memberships', function() {
    	var UserAffiliateService, affiliate,
    		ihtsdoMember = { key:'IHTSDO' },
    		swedenMember = { key:'SE' },
    		franceMember = { key:'FR' }
    		;
    	
        beforeEach(module(function($provide) {
      	}));

    	beforeEach(inject(function (_UserAffiliateService_) {
    		UserAffiliateService = _UserAffiliateService_;
        }));

    	it('should have no memberships if no applications', function() {
    		affiliate = {
    			applications: []	
    		};
    		UserAffiliateService.setAffiliate(affiliate);
    		
    		expect(UserAffiliateService.affiliate).toEqual(affiliate);
    		expect(UserAffiliateService.approvedMemberships).toEqual([]);
    		expect(UserAffiliateService.incompleteMemberships).toEqual([]);
    	});

    	it('should be able to match applications to membership state', function() {
    		affiliate = {
    			applications: [
    			               {
    			            	   approvalState: 'APPROVED',
    			            	   member: ihtsdoMember
    			               },
    			               {
    			            	   approvalState: 'SUBMITTED',
    			            	   member: swedenMember
    			               }
    			               ]	
    		};
    		UserAffiliateService.setAffiliate(affiliate);
    		
    		expect(UserAffiliateService.approvedMemberships).toEqual([ihtsdoMember]);
    		expect(UserAffiliateService.incompleteMemberships).toEqual([swedenMember]);
    	});

    	it('should confer ihtsdo approved membership if only have territory membership', function() {
    		affiliate = {
    			applications: [
    			               {
    			            	   approvalState: 'APPROVED',
    			            	   member: swedenMember
    			               },
    			               ]	
    		};
    		UserAffiliateService.setAffiliate(affiliate);
    		
    		expect(UserAffiliateService.approvedMemberships).toEqual([swedenMember, ihtsdoMember]);
    		expect(UserAffiliateService.incompleteMemberships).toEqual([]);
    	});

    	it('should confer ihtsdo incomplete membership if only have territory membership', function() {
    		affiliate = {
    			applications: [
    			               {
    			            	   approvalState: 'SUBMITTED',
    			            	   member: swedenMember
    			               },
    			               ]	
    		};
    		UserAffiliateService.setAffiliate(affiliate);
    		
    		expect(UserAffiliateService.approvedMemberships).toEqual([]);
    		expect(UserAffiliateService.incompleteMemberships).toEqual([swedenMember, ihtsdoMember]);
    	});

    	it('should be ignore REJECTED applications', function() {
    		affiliate = {
    			applications: [
    			               {
    			            	   approvalState: 'REJECTED',
    			            	   member: ihtsdoMember
    			               },
    			               {
    			            	   approvalState: 'REJECTED',
    			            	   member: swedenMember
    			               }
    			               ]	
    		};
    		UserAffiliateService.setAffiliate(affiliate);
    		
    		expect(UserAffiliateService.approvedMemberships).toEqual([]);
    		expect(UserAffiliateService.incompleteMemberships).toEqual([]);
    	});

    });
    
    describe('membership utilities', function() {
    	var UserAffiliateService,
    		ihtsdoMember = { key:'IHTSDO' },
    		swedenMember = { key:'SE' },
    		franceMember = { key:'FR' }
    		;
    	
        beforeEach(module(function($provide) {
      	}));

    	beforeEach(inject(function (_UserAffiliateService_) {
    		UserAffiliateService = _UserAffiliateService_;
        }));
    	
    	it('should be able to test for approved', function() {
    		UserAffiliateService.approvedMemberships = [ihtsdoMember];
    		
    		expect(UserAffiliateService.isMembershipApproved(ihtsdoMember)).toBeTruthy();
    		
    		expect(UserAffiliateService.isMembershipApproved(swedenMember)).not.toBeTruthy();
    	});

    	it('should be able to test for incomplete', function() {
    		UserAffiliateService.incompleteMemberships = [ihtsdoMember];
    		
    		expect(UserAffiliateService.isMembershipIncomplete(ihtsdoMember)).toBeTruthy();
    		
    		expect(UserAffiliateService.isMembershipIncomplete(swedenMember)).not.toBeTruthy();
    	});

    	it('should be able to test for not-started', function() {
    		UserAffiliateService.approvedMemberships = [ihtsdoMember];
    		UserAffiliateService.incompleteMemberships = [swedenMember];
    		
    		expect(UserAffiliateService.isMembershipNotStarted(franceMember)).toBeTruthy();
    		
    		expect(UserAffiliateService.isMembershipNotStarted(ihtsdoMember)).not.toBeTruthy();
    		expect(UserAffiliateService.isMembershipNotStarted(swedenMember)).not.toBeTruthy();
    	});

});
});