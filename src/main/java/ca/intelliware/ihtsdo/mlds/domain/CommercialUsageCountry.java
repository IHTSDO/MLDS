package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="commercial_usage_count")
public class CommercialUsageCountry extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="commercial_usage_count_id")
	Long commercialUsageCountId;

	// the parent
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="commercial_usage_id")
	CommercialUsage commercialUsage;
	
	@ManyToOne
	Country country;

	Instant created = Instant.now();
	
	Integer practices = 0;

	public CommercialUsageCountry() {
		
	}
	
	// For Testing
	public CommercialUsageCountry(long commercialUsageCountId, CommercialUsage commercialUsage) {
		this.commercialUsageCountId = commercialUsageCountId;
		commercialUsage.addCount(this);
	}
	
	public Long getCommercialUsageCountId() {
		return commercialUsageCountId;
	}
	
	public void setCommercialUsageCountId(Long commercialUsageCountId) {
		this.commercialUsageCountId = commercialUsageCountId;
	}

	public Country getCountry() {
		return country;
	}

	public Integer getPractices() {
		return practices;
	}

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}

	@Override
	protected Object getPK() {
		return commercialUsageCountId;
	}

}
