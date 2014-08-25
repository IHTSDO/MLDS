'use strict';

describe('ApplicationUtilsService Tests ', function () {
	var allApprovalStates = ['NOT_SUBMITTED', 'SUBMITTED', 'CHANGE_REQUESTED', 'RESUBMITTED', 'REVIEW_REQUESTED', 'APPROVED', 'REJECTED'];
	
    beforeEach(module('MLDS'));
    
    describe('application state utilities', function() {
    	var ApplicationUtilsService;
    	
        beforeEach(module(function($provide) {
      	}));

    	beforeEach(inject(function (_ApplicationUtilsService_) {
    		ApplicationUtilsService = _ApplicationUtilsService_;
        }));

    	function applicationWithApprovalState(state) {
    		return {
    			approvalState: state
    		};
    	}
    	
    	function expectTruthyFor(func, shouldBeTruthy) {
    		//Ensure known approval state
    		_.each(shouldBeTruthy, function(state) {
    			expect(_.contains(allApprovalStates, state)).toBeTruthy();
    		});
    		
    		_.each(allApprovalStates, function(state) {
    			var application = applicationWithApprovalState(state);
    			var result = func(application);
    			if (_.contains(shouldBeTruthy, state)) {
    				expect(result).toBeTruthy();
    			} else {
    				expect(result).not.toBeTruthy();
    			}
    		});
    	}
    	
    	//TODO these tests feel like pure duplication of actual code... Rewrite/remove?
    	
    	it('should be able to test isApplicationWaitingForApplicant', function() {
    		expectTruthyFor(ApplicationUtilsService.isApplicationWaitingForApplicant, ['NOT_SUBMITTED', 'CHANGE_REQUESTED']);
    	});

    	it('should be able to test isApplicationApproved', function() {
    		expectTruthyFor(ApplicationUtilsService.isApplicationApproved, ['APPROVED']);
    	});

    	it('should be able to test isApplicationRejected', function() {
    		expectTruthyFor(ApplicationUtilsService.isApplicationRejected, ['REJECTED']);
    	});

    	it('should be able to test isApplicationIncomplete', function() {
    		expectTruthyFor(ApplicationUtilsService.isApplicationIncomplete, ['NOT_SUBMITTED', 'SUBMITTED', 'CHANGE_REQUESTED', 'RESUBMITTED', 'REVIEW_REQUESTED']);
    	});

    	it('should be able to test isApplicationPending', function() {
    		expectTruthyFor(ApplicationUtilsService.isApplicationPending, ['SUBMITTED', 'RESUBMITTED', 'REVIEW_REQUESTED']);
    	});

    });
});