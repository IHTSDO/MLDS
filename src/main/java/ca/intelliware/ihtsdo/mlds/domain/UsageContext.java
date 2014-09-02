package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UsageContext {

	@Column(name="current_usage")
	String currentUsage;
	
	@Column(name="planned_usage")
	String plannedUsage;
	
	String purpose;
	
	@Column(name="implementation_status")
	String implementationStatus;
	
	public String getCurrentUsage() {
		return currentUsage;
	}

	public String getPlannedUsage() {
		return plannedUsage;
	}

	public String getPurpose() {
		return purpose;
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

	public String getImplementationStatus() {
		return implementationStatus;
	}

}
