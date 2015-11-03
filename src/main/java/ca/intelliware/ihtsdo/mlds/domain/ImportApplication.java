package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@DiscriminatorValue("IMPORT")
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE application SET inactive_at = now() WHERE application_id = ?")
public class ImportApplication extends PrimaryApplication {

	@Override
	public ApplicationType getApplicationType() {
		return Application.ApplicationType.IMPORT;
	}

}
