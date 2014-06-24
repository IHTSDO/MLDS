package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;

// FIXME MB we need an equals.  Can we put that in a base?
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
	
	// FIXME MLDS-23 add submitted-state enum
	
	// FIXME MLDS-32 add a createdBy?  Or generate an entry in the log?
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="commercialUsage")
	Set<CommercialUsageEntry> usage = Sets.newHashSet();
	
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

	public void addEntry(CommercialUsageEntry newEntryValue) {
		Validate.notNull(newEntryValue.commercialUsageEntryId);
		
		if (newEntryValue.commercialUsage != null) {
			newEntryValue.commercialUsage.usage.remove(newEntryValue);
		}
		newEntryValue.commercialUsage = this;
		usage.add(newEntryValue);
	}

	public Set<CommercialUsageEntry> getEntries() {
		return Collections.unmodifiableSet(usage);
	}
}
