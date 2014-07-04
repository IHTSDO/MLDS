package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class UsageContext {

	@Column(name="current_usage")
	String currentUsage;
	
	@Column(name="planned_usage")
	String plannedUsage;
	
	String purpose;
	
	@Enumerated(EnumType.STRING)
	@Column(name="agreement_type")
	AgreementType agreementType;

	public String getCurrentUsage() {
		return currentUsage;
	}

	public String getPlannedUsage() {
		return plannedUsage;
	}

	public String getPurpose() {
		return purpose;
	}

	public AgreementType getAgreementType() {
		return agreementType;
	}

	public void setCurrentUsage(String currentUsage) {
		this.currentUsage = currentUsage;
	}

	public void setPlannedUsage(String plannedUsage) {
		this.plannedUsage = plannedUsage;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public void setAgreementType(AgreementType agreementType) {
		this.agreementType = agreementType;
	}

}
