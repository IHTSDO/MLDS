package ca.intelliware.ihtsdo.mlds.domain;



import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hibernate.annotations.SQLDelete;

@Entity
@DiscriminatorValue("IMPORT")
@SQLDelete(sql="UPDATE application SET inactive_at = now() WHERE application_id = ?")
public class ImportApplication extends PrimaryApplication {

	@Override
	public ApplicationType getApplicationType() {
		return Application.ApplicationType.IMPORT;
	}

}
