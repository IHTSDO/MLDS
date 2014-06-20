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

@Entity
@Table(name="commercial_usage")
public class CommercialUsage {
	@Id
	@Column(name="commercial_usage_id")
	long commercialUsageId;

	// FIXME MLDS-23 some kind of token for submission year, or "current"
	String key; // current, or declaration label
	
	Instant created;
	
	// FIXME MLDS-32 add a createdBy?  Or generate an entry in the log?
	
	@ManyToMany(cascade=CascadeType.PERSIST)
	@JoinTable(
			joinColumns=@JoinColumn(name="commercial_usage_id"),
			inverseJoinColumns=@JoinColumn(name="commercial_usage_entry_id")
	)
	Set<CommercialUsageEntry> usage;
}
