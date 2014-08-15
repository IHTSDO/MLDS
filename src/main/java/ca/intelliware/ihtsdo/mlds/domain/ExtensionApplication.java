package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("EXTENSION")
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

}
