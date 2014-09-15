package ca.intelliware.ihtsdo.mlds.domain;

public enum StandingState {
	APPLYING,
	IN_GOOD_STANDING,
	
	REJECTED,
	
	DEACTIVATION_PENDING,
	DEACTIVATED,
	
	DEREGISTRATION_PENDING,
	DEREGISTERED;
	
	public boolean canLogin() {
		return this != DEREGISTERED; 
	}
}
