package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

/**
 * Bean to wrap json response
 * {"perms":[{"app":"Release","role":"Manager","member":"UK"}]}
 */
public class CentralAuthUserPermission {
	String app;
	String role;
	String member;
	
	public CentralAuthUserPermission() {
	}

	public CentralAuthUserPermission(String app, String role, String member) {
		this();
		this.app = app;
		this.role = role;
		this.member = member;
	}

	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getMember() {
		return member;
	}
	public void setMember(String member) {
		this.member = member;
	}
}