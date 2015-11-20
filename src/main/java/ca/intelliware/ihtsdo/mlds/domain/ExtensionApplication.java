package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("EXTENSION")
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE application SET inactive_at = now() WHERE application_id = ?")
public class ExtensionApplication extends Application {
	String reason;

	public ExtensionApplication() {}
	public ExtensionApplication(long id) {
		this();
		this.applicationId = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Override
	public ApplicationType getApplicationType() {
		return Application.ApplicationType.EXTENSION;
	}

}
