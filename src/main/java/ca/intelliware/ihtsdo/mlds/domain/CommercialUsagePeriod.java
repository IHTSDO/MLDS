package ca.intelliware.ihtsdo.mlds.domain;

import org.joda.time.LocalDate;

public class CommercialUsagePeriod {
	LocalDate startDate;
	LocalDate endDate;
	String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
