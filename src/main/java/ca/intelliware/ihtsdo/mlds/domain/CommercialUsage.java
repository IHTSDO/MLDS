package ca.intelliware.ihtsdo.mlds.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="commercial_usage")
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE commercial_usage SET inactive_at = now() WHERE commercial_usage_id = ?")
public class CommercialUsage extends BaseEntity {
	@Id
//	@GeneratedValue
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_demo")
	@SequenceGenerator(name = "hibernate_demo", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="commercial_usage_id")
	Long commercialUsageId;

	// the parent
	//FIXME review dependency graph!
	@JsonIgnoreProperties(value={"application", "applications", "commercialUsages"}, allowSetters=true)
	@ManyToOne
	@JoinColumn(name="affiliate_id")
	Affiliate affiliate;

	@Enumerated(EnumType.STRING)
	AffiliateType type;

	//@Type(type="jodatimeInstant")
	Instant created = Instant.now();
	
	@JsonIgnore
	@Column(name="inactive_at")
	Instant inactiveAt;
	
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
	@Where(clause = "inactive_at IS NULL")
	Set<CommercialUsageEntry> usage = new HashSet<>() /*Sets.newHashSet()*/;
	
	@JsonProperty("countries")
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="commercialUsage")
	@Where(clause = "inactive_at IS NULL")
	Set<CommercialUsageCountry> countries = new HashSet<>() /*Sets.newHashSet()*/;
	
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

	public void setCountries(Set<CommercialUsageCountry> countries) {
		this.countries = countries;
	}
	
	public void setUsage(Set<CommercialUsageEntry> usage) {
		this.usage = usage;
	}
}
