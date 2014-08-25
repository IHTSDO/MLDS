'use strict';

describe('ApprovalStateUtils Tests ', function () {
	var allApprovalStates = ['NOT_SUBMITTED', 'SUBMITTED', 'CHANGE_REQUESTED', 'RESUBMITTED', 'REVIEW_REQUESTED', 'APPROVED', 'REJECTED'];
	
    beforeEach(module('MLDS'));
    
    describe('ApprovalState utilities', function() {
    	var ApprovalStateUtils = null;
    	
        beforeEach(module(function($provide) {
      	}));

    	beforeEach(inject(function (_ApprovalStateUtils_) {
    		ApprovalStateUtils = _ApprovalStateUtils_;
        }));

    	function expectTruthyFor(func, shouldBeTruthy) {
    		//Ensure known approval state
    		_.each(shouldBeTruthy, function(state) {
    			expect(_.contains(allApprovalStates, state)).toBeTruthy();
    		});
    		
    		_.each(allApprovalStates, function(state) {
    			var result = func(state);
    			if (_.contains(shouldBeTruthy, state)) {
    				expect(result).toBeTruthy();
    			} else {
    				expect(result).not.toBeTruthy();
    			}
    		});
    	}
    	
    	//FIXME these tests feel like pure duplication of actual code... Rewrite/remove?
    	
    	it('should be able to test isWaitingForApplicant', function() {
    		expectTruthyFor(ApprovalStateUtils.isWaitingForApplicant, ['NOT_SUBMITTED', 'CHANGE_REQUESTED']);
    	});

    	it('should be able to test isApproved', function() {
    		expectTruthyFor(ApprovalStateUtils.isApproved, ['APPROVED']);
    	});

    	it('should be able to test isRejected', function() {
    		expectTruthyFor(ApprovalStateUtils.isRejected, ['REJECTED']);
    	});

    	it('should be able to test isIncomplete', function() {
    		expectTruthyFor(ApprovalStateUtils.isIncomplete, ['NOT_SUBMITTED', 'SUBMITTED', 'CHANGE_REQUESTED', 'RESUBMITTED', 'REVIEW_REQUESTED']);
    	});

    	it('should be able to test isPending', function() {
    		expectTruthyFor(ApprovalStateUtils.isPending, ['SUBMITTED', 'RESUBMITTED', 'REVIEW_REQUESTED']);
    	});
    });
});