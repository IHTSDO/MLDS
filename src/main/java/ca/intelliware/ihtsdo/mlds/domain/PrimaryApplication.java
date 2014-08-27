package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("PRIMARY")
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

	public boolean isSnoMedLicence() {
		return snomedlicense;
	}

	public void setSnoMedLicence(boolean snoMedLicence) {
		this.snomedlicense = snoMedLicence;
	}

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}

	public void setCommercialUsage(CommercialUsage commercialUsage) {
		this.commercialUsage = commercialUsage;
	}

}
