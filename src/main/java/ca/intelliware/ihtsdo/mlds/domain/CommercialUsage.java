package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

@Entity
@Table(name="commercial_usage")
public class CommercialUsage {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_id")
	Long commercialUsageId;

	//@Type(type="jodatimeInstant")
	Instant created = Instant.now();
	
	@Column(name="start_date")
	LocalDate startDate;

	@Column(name="end_date")
	LocalDate endDate;
	
	// FIXME MLDS-32 add a createdBy?  Or generate an entry in the log?
	
	@OneToMany(cascade=CascadeType.PERSIST)
	Set<CommercialUsageEntry> usage;
	
	public Long getCommercialUsageId() {
		return commercialUsageId;
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

	public Instant getCreated() {
		return created;
	}

}
