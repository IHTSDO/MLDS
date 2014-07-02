package ca.intelliware.ihtsdo.mlds.domain;

import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.Validate;
import org.joda.time.Instant;

import ca.intelliware.ihtsdo.mlds.registration.Application;

import com.google.common.collect.Sets;

@Entity
public class Licensee {
	@Id
	@GeneratedValue
	@Column(name="licensee_id")
	Long licenseeId;

	//@Type(type="jodatimeInstant")
	Instant created = Instant.now();

	@Enumerated(EnumType.STRING)
	LicenseeType type;
	
	//FIXME username of user
	String creator;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="licensee")
	Set<CommercialUsage> commercialUsages = Sets.newHashSet();

	@OneToOne()
	@JoinColumn(name="application_id")
	Application application;
	
	public void addCommercialUsage(CommercialUsage newEntryValue) {
		Validate.notNull(newEntryValue.getCommercialUsageId());
		
		if (newEntryValue.licensee != null) {
			newEntryValue.licensee.commercialUsages.remove(newEntryValue);
		}
		newEntryValue.licensee = this;
		commercialUsages.add(newEntryValue);
	}

	public Set<CommercialUsage> getCommercialUsages() {
		return Collections.unmodifiableSet(commercialUsages);
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public LicenseeType getType() {
		return type;
	}
}
