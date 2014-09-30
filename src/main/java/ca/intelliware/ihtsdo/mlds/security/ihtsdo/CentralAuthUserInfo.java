package ca.intelliware.ihtsdo.mlds.security.ihtsdo;


/**
 * Bean to wrap json response from https://usermanagement.ihtsdotools.org/security-web/query/users/Bob
 * {
 * 	"name":"Bob",
 * 	"status":"ENABLED",
 * 	"email":"bob@test.com",
 * 	"givenName":"Bob",
 * 	"middleName":"the",
 * 	"surname":"Bobbin",
 *      "parentDir":"OTF Users",
 * 	"token":"411f228b-7e48-4449-8432-8f7416692be9"
 * }
 */
public class CentralAuthUserInfo {
	static enum Status {
		ENABLED, DISABLED;
	}
	String name;
	Status status;
	String email;
	String givenName;
	String middleName;
	String surname;
	String token;
	String parentDir;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getParentDir() {
		return parentDir;
	}
	public void setParentDir(String parentDir) {
		this.parentDir = parentDir;
	}
}