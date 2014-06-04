package ca.intelliware.ihtsdo.mlds.registration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="application")
public class Application {
	@Id
	@GeneratedValue
	@Column(name="application_id")
    private Long applicationId;
	
	String username;
	boolean approved;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}


	public boolean isApproved() {
		return approved;
	}

}
