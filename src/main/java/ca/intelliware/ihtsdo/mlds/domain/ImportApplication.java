package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import ca.intelliware.ihtsdo.mlds.domain.Application.ApplicationType;

@Entity
@DiscriminatorValue("IMPORT")
public class ImportApplication extends PrimaryApplication {

	@Override
	public ApplicationType getApplicationType() {
		return Application.ApplicationType.IMPORT;
	}

}
