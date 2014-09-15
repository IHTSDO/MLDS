'use strict';

describe('StandingStateUtils Tests ', function () {
	var allStates = ['APPLYING',
	             	'IN_GOOD_STANDING',
	            	
	            	'REJECTED',
	            	
	            	'DEACTIVATION_PENDING',
	            	'DEACTIVATED',
	            	
	            	'DEREGISTRATION_PENDING',
	            	'DEREGISTERED'];
	
    beforeEach(module('MLDS'));
    
    describe('StandingState utilities', function() {
    	var StandingStateUtils = null;
    	
        beforeEach(module(function($provide) {
      	}));

    	beforeEach(inject(function (_StandingStateUtils_) {
    		StandingStateUtils = _StandingStateUtils_;
        }));

    	function expectTruthyFor(func, shouldBeTruthy) {
    		//Ensure known approval state
    		_.each(shouldBeTruthy, function(state) {
    			expect(_.contains(allStates, state)).toBeTruthy();
    		});
    		
    		_.each(allStates, function(state) {
    			var result = func(state);
    			if (_.contains(shouldBeTruthy, state)) {
    				expect(result).toBeTruthy();
    			} else {
    				expect(result).not.toBeTruthy();
    			}
    		});
    	}
    	
    	//FIXME these tests feel like pure duplication of actual code... Rewrite/remove?
    	
    	it('should be able to test isSuccessCategory', function() {
    		expectTruthyFor(StandingStateUtils.isSuccessCategory, ['IN_GOOD_STANDING']);
    	});
    	it('should be able to test isWarningCategory', function() {
    		expectTruthyFor(StandingStateUtils.isWarningCategory, ['APPLYING', 'DEACTIVATION_PENDING', 'DEREGISTRATION_PENDING']);
    	});
    	it('should be able to test isDangerCategory', function() {
    		expectTruthyFor(StandingStateUtils.isDangerCategory, ['REJECTED', 'DEACTIVATED', 'DEREGISTERED']);
    	});
});
});