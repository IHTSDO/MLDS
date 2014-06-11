package ca.intelliware.ihtsdo.mlds.registration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user_registration")
public class UserRegistration {
	@Id
	@Column(name="user_registration_id")
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
