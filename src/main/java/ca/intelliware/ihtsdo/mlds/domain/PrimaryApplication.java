package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("PRIMARY")
public class PrimaryApplication extends Application{

	@Enumerated(EnumType.STRING)
	AffiliateType type;
	@Enumerated(EnumType.STRING)
	@Column(name = "subtype")
	AffiliateSubType subType;
	@Column(name = "other_text")
	String otherText;
	boolean snomedlicense;
	@ManyToOne
	@JoinColumn(name = "commercial_usage_id")
	CommercialUsage commercialUsage;

	public PrimaryApplication() { }
	
	public PrimaryApplication(long id) {
		super(id);
	}

	public AffiliateSubType getSubType() {
		return subType;
	}

	public void setSubType(AffiliateSubType subType) {
		this.subType = subType;
	}

	public AffiliateType getType() {
		return type;
	}

	public void setType(AffiliateType type) {
		this.type = type;
	}

	public boolean isSnoMedLicence() {
		return snomedlicense;
	}

	public void setSnoMedLicence(boolean snoMedLicence) {
		this.snomedlicense = snoMedLicence;
	}

	public String getOtherText() {
		return otherText;
	}

	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}

	public void setCommercialUsage(CommercialUsage commercialUsage) {
		this.commercialUsage = commercialUsage;
	}

}
