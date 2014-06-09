package ca.intelliware.ihtsdo.mlds.registration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserRegistration {
	@Id
	@GeneratedValue
    private Long userRegistrationId;

	String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	} 
}
