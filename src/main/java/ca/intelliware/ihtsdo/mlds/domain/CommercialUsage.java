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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

@Entity
@Table(name="commercial_usage")
public class CommercialUsage extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_id")
	Long commercialUsageId;

	// the parent
	//FIXME review dependency graph!
	@JsonIgnoreProperties({"application", "applications", "commercialUsages"})
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
	@Column(name = "state")
	private UsageReportState state;
	
	private String note;
	
	@Column(name="effective_to")
	private Instant effectiveTo;

	@Embedded
	UsageContext context;
	
	//@Type(type="jodatimeInstant")
	private Instant submitted = null;

	@JsonProperty("entries")
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="commercialUsage")
	Set<CommercialUsageEntry> usage = Sets.newHashSet();
	
	@JsonProperty("countries")
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="commercialUsage")
	Set<CommercialUsageCountry> countries = Sets.newHashSet();
	
	public CommercialUsage() {
		
	}
	
	// For Testing
	public CommercialUsage(Long commercialUsageId, Affiliate affiliate) {
		this.commercialUsageId = commercialUsageId;
		this.affiliate = affiliate;
	}
	
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

	@JsonIgnore
	public Set<CommercialUsageEntry> getEntries() {
		return Collections.unmodifiableSet(usage);
	}


	public void addCount(CommercialUsageCountry newCountryValue) {
		Validate.notNull(newCountryValue.commercialUsageCountId);
		
		if (newCountryValue.commercialUsage != null) {
			//Not sure why we're asking the incoming object for a reference to 'this'
			//Think I'll blow up if the two aren't one and the same object
			if (newCountryValue.commercialUsage.commercialUsageId != this.commercialUsageId) {
				throw new IllegalArgumentException("Object owner incompatibility between commercial usage object "
						+ newCountryValue.commercialUsage.commercialUsageId + " and " + this.commercialUsageId);
			}
			//newCountryValue.commercialUsage.countries.remove(newCountryValue);
			countries.remove(newCountryValue);
		}
		newCountryValue.commercialUsage = this;
		countries.add(newCountryValue);
	}

	@JsonIgnore
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

	public UsageReportState getState() {
		return state;
	}

	public void setState(UsageReportState state) {
		this.state = state;
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

	public Instant getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(Instant effectiveTo) {
		this.effectiveTo = effectiveTo;
	}
	
	@JsonIgnore
	public boolean isActive() {
		return getEffectiveTo() == null;
	}

	public boolean exists(CommercialUsageCountry newCountValue) {
		// Does this usage report have a country count for this country?
		String newCode = newCountValue.getCountry().getIsoCode2();
		for (CommercialUsageCountry countryCount : countries) {
			if (countryCount.getCountry().getIsoCode2().equalsIgnoreCase(newCode)) {
				return true;
			}
		}
		return false;
	}
}
