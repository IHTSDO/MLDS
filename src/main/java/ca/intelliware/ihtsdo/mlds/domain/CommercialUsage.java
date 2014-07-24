package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

// FIXME MB we need an equals.  Can we put that in a base?
@Entity
@Table(name="commercial_usage")
public class CommercialUsage extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_id")
	Long commercialUsageId;

	// the parent
	//FIXME review dependency graph!
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="affiliate_id")
	Affiliate affiliate;

	@Enumerated(EnumType.STRING)
	AffiliateType type;

	//@Type(type="jodatimeInstant")
	Instant created = Instant.now();
	
	@Column(name="start_date")
	LocalDate startDate;

	@Column(name="end_date")
	LocalDate endDate;

	@Enumerated(EnumType.STRING)
	@Column(name="approval_state")
	private ApprovalState approvalState;
	
	private String note;

	@Embedded
	UsageContext context;
	
	//@Type(type="jodatimeInstant")
	private Instant submitted = null;

	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="commercialUsage")
	Set<CommercialUsageEntry> usage = Sets.newHashSet();
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="commercialUsage")
	Set<CommercialUsageCountry> countries = Sets.newHashSet();
	
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


	public void addCount(CommercialUsageCountry newCountryValue) {
		Validate.notNull(newCountryValue.commercialUsageCountId);
		
		if (newCountryValue.commercialUsage != null) {
			newCountryValue.commercialUsage.countries.remove(newCountryValue);
		}
		newCountryValue.commercialUsage = this;
		countries.add(newCountryValue);
	}

	public Set<CommercialUsageCountry> getCountries() {
		return Collections.unmodifiableSet(countries);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Instant getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Instant submitted) {
		this.submitted = submitted;
	}

	public ApprovalState getApprovalState() {
		return approvalState;
	}

	public void setApprovalState(ApprovalState approvalState) {
		this.approvalState = approvalState;
	}

	public void setCommercialUsageId(Long commercialUsageId) {
		this.commercialUsageId = commercialUsageId;
	}

	public Affiliate getAffiliate() {
		return affiliate;
	}

	public UsageContext getContext() {
		return context;
	}

	public void setContext(UsageContext context) {
		this.context = context;
	}

	public AffiliateType getType() {
		return type;
	}

	public void setType(AffiliateType type) {
		this.type = type;
	}

	@Override
	protected Object getPK() {
		return commercialUsageId;
	}

}
