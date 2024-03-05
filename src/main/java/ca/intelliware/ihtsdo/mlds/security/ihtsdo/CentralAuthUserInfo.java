package ca.intelliware.ihtsdo.mlds.security.ihtsdo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Bean to wrap json response from https://ims.ihtsdotools.org:443/api/account
{
  "login": "pwilliams",
  "password": null,
  "firstName": "Peter",
  "lastName": "G. Williams",
  "email": "pwi@ihtsdo.org",
  "langKey": null,
  "roles": [
    "ROLE_auths-design-steering-group",
    "ROLE_auths-extended-team",
    "ROLE_auths-testers"
  ]
}
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CentralAuthUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	String login;
	String firstName;
	String email;
	String lastName;
	List<String> roles;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    @Override
    public String toString() {
        return "CentralAuthUserInfo{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", email='" + email + '\'' +
            ", lastName='" + lastName + '\'' +
            ", roles=" + roles +
            '}';
    }
}
