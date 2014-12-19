package ca.intelliware.ihtsdo.mlds.domain;

public enum StandingState {
	APPLYING,
	IN_GOOD_STANDING,
	
	REJECTED,
	
	DEACTIVATION_PENDING,
	DEACTIVATED,
	
	DEREGISTERED,
	
	PENDING_INVOICE,
	INVOICE_SENT;
	
	public boolean canLogin() {
		return this != DEREGISTERED; 
	}
}
