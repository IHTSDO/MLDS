package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("IMPORT")
public class ImportApplication extends PrimaryApplication {

	@Override
	public ApplicationType getApplicationType() {
		return Application.ApplicationType.IMPORT;
	}

}
