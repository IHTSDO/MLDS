package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;



@Entity
@DiscriminatorValue("PRIMARY")
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE application SET inactive_at = now() WHERE application_id = ?")
public class PrimaryApplication extends Application{

	boolean snomedlicense;
	
	@ManyToOne
	@JoinColumn(name = "commercial_usage_id")
	CommercialUsage commercialUsage;

	public PrimaryApplication() { }
	
	public PrimaryApplication(long id) {
		super(id);
	}

	@Deprecated
	public AffiliateSubType getSubType() {
		return affiliateDetails==null?null:affiliateDetails.getSubType();
	}
	
	@Deprecated
	public void setSubType(AffiliateSubType ignore) {
		//ignore
	}
	

	@Deprecated
	public AffiliateType getType() {
		return affiliateDetails==null?null:affiliateDetails.getType();
	}
	
	@Deprecated
	public void setType(AffiliateType ignore) {
		// ignore
	}

	@Deprecated
	public String getOtherText() {
		return affiliateDetails==null?null:affiliateDetails.getOtherText();
	}
	
	@Deprecated
	public void setOtherText(String ignore) {
		// ignore
	}

	public boolean isSnoMedLicense() {
		return snomedlicense;
	}

	public void setSnoMedLicense(boolean snoMedLicense) {
		this.snomedlicense = snoMedLicense;
	}

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}

	public void setCommercialUsage(CommercialUsage commercialUsage) {
		this.commercialUsage = commercialUsage;
	}
	
	@Override
	public ApplicationType getApplicationType() {
		return ApplicationType.PRIMARY;
	}

}
