package ca.intelliware.ihtsdo.mlds.web;

import ca.intelliware.ihtsdo.mlds.registration.UserRegistration;

public class UserInfo {
	UserRegistration userRegistration;
	boolean hasApplied = false;
	boolean isApproved = false;

	public boolean getHasApplied() {
		return hasApplied;
	}

	public void setHasApplied(boolean hasApplied) {
		this.hasApplied = hasApplied;
	}
	
	public void setUserRegistration(UserRegistration currentRegistration) {
		this.userRegistration = currentRegistration;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public UserRegistration getUserRegistration() {
		return userRegistration;
	}

}