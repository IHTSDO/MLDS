package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

@Entity
@Table(name="commercial_usage_entry")
public class CommercialUsageEntry {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_entry_id")
	Long commercialUsageEntryId;

	String name;
	
	@Column(name="start_date", nullable=false)
	LocalDate startDate;
	
	@Column(name="end_date")
	LocalDate endDate;
	
	@ManyToOne
	Country country;
	
	Instant created;

	public long getCommercialUsageEntryId() {
		return commercialUsageEntryId;
	}

	public String getName() {
		return name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public Country getCountry() {
		return country;
	}

	public Instant getCreated() {
		return created;
	}
}
