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

	public boolean isApproved() {
		return approved;
	}

}
