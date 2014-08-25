'use strict';

describe('MemberService Tests ', function () {

    beforeEach(module('MLDS'));
    
    describe('member utilities', function() {
    	var MemberService,
    		ihtsdoMember = { key:'IHTSDO' },
    		swedenMember = { key:'SE' };
    	
        beforeEach(module(function($provide) {
      	}));

    	beforeEach(inject(function (_MemberService_) {
            MemberService = _MemberService_;
        }));
    	
    	it('should be able to match ihtsdo member', function() {
    		expect(MemberService.isIhtsdoMember(ihtsdoMember)).toBeTruthy();
    		expect(MemberService.isIhtsdoMember(swedenMember)).not.toBeTruthy();

    		expect(MemberService.isIhtsdoMember(null)).not.toBeTruthy();
    	});

    	it('should be able do equality checks between members', function() {
    		expect(MemberService.isMemberEqual(ihtsdoMember, ihtsdoMember)).toBeTruthy();
    		expect(MemberService.isMemberEqual(swedenMember, swedenMember)).toBeTruthy();
    		expect(MemberService.isMemberEqual(ihtsdoMember, swedenMember)).not.toBeTruthy();
    		expect(MemberService.isMemberEqual(swedenMember, ihtsdoMember)).not.toBeTruthy();
    		expect(MemberService.isMemberEqual(swedenMember, null)).not.toBeTruthy();
    		expect(MemberService.isMemberEqual(null, ihtsdoMember)).not.toBeTruthy();
    		expect(MemberService.isMemberEqual(null, null)).not.toBeTruthy();
    	});
});
});