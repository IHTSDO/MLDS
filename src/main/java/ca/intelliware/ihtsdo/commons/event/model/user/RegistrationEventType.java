package ca.intelliware.ihtsdo.commons.event.model.user;

public enum RegistrationEventType {

	NEW_USER_CREATION("New user registered"), 
	TOS_AGREEMENT("User accepted TOS"), 
	VERIFICATION_REQUEST_RESENT("Verification resent"), 
	NEW_USER_VERIFIED("New user verified");
	
	private final String description;

	private RegistrationEventType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
