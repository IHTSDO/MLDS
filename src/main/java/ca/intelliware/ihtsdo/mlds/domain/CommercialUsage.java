package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

@Entity
@Table(name="commercial_usage")
public class CommercialUsage {
	@Id
	@Column(name="commercial_usage_id")
	long commercialUsageId;

	Instant created;
	
	@Column(name="start_date")
	LocalDate startDate;
	
	@Column(name="end_date")
	LocalDate endDate;
	
	// FIXME MLDS-32 add a createdBy?  Or generate an entry in the log?
	
	// FIXME MLDS-32 change to one-to-many, drop join table, add parent key to CommercialUsageEntry
	@ManyToMany(cascade=CascadeType.PERSIST)
	@JoinTable(
			joinColumns=@JoinColumn(name="commercial_usage_id"),
			inverseJoinColumns=@JoinColumn(name="commercial_usage_entry_id")
	)
	Set<CommercialUsageEntry> usage;
}
