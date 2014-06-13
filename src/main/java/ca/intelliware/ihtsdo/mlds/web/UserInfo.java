package ca.intelliware.ihtsdo.mlds.web;

import ca.intelliware.ihtsdo.mlds.domain.User;

public class UserInfo {
	User user;
	boolean hasApplied = false;
	boolean isApproved = false;

	public boolean getHasApplied() {
		return hasApplied;
	}

	public void setHasApplied(boolean hasApplied) {
		this.hasApplied = hasApplied;
	}
	
	public void setUser(User currentUser) {
		this.user = currentUser;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public User getUser() {
		return user;
	}

}