package ca.intelliware.ihtsdo.mlds.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="commercial_usage_entry")
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE commercial_usage_entry SET inactive_at = now() WHERE commercial_usage_entry_id = ?")
public class CommercialUsageEntry extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
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

	@JsonIgnore
	@Column(name="inactive_at")
	private
	Instant inactiveAt;

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

	public Instant getInactiveAt() {
		return inactiveAt;
	}

	public void setInactiveAt(Instant inactiveAt) {
		this.inactiveAt = inactiveAt;
	}
}
