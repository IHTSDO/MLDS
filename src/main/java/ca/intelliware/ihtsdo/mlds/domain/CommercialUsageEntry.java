package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="commercial_usage_entry")
public class CommercialUsageEntry extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_entry_id")
	Long commercialUsageEntryId;

	// the parent
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="commercial_usage_id")
	CommercialUsage commercialUsage;
	
	String name;
	
	String note;
	
	@Column(name="start_date")
	LocalDate startDate;
	
	@Column(name="end_date")
	LocalDate endDate;
	
	@ManyToOne
	Country country;
	
	Instant created = Instant.now();
	
	public CommercialUsageEntry() {
		
	}
	
	//For testing
	public CommercialUsageEntry(long commercialUsageEntryId, CommercialUsage commercialUsage) {
		this.commercialUsageEntryId = commercialUsageEntryId;
		commercialUsage.addEntry(this);
	}
	
	public Long getCommercialUsageEntryId() {
		return commercialUsageEntryId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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

	public String getNote() {
		return note;
	}

	public void setCommercialUsageEntryId(Long commercialUsageEntryId) {
		this.commercialUsageEntryId = commercialUsageEntryId;
	}

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}

	@Override
	protected Object getPK() {
		return commercialUsageEntryId;
	}
}
