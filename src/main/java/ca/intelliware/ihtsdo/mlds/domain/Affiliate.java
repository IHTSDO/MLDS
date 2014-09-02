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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.Validate;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;

@Entity
@Indexed
@JsonFilter("affiliatePrivacyFilter")
public class Affiliate extends BaseEntity {
	public static final String[] PRIVATE_FIELDS = {"notesInternal"};

	@Id
	@GeneratedValue
	@Column(name="affiliate_id")
	@Fields({ @Field(name="ALL"), @Field()})
	Long affiliateId;

	//@Type(type="jodatimeInstant")
	Instant created = Instant.now();

	@Enumerated(EnumType.STRING)
	AffiliateType type;
	
	//FIXME username of user
	String creator;
	
	@Column(name="import_key")
	String importKey;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="affiliate")
	Set<CommercialUsage> commercialUsages = Sets.newHashSet();

	@OneToOne()
	@JoinColumn(name="affiliate_details_id")
	@IndexedEmbedded(prefix="")
	AffiliateDetails affiliateDetails;
	
	@JsonIgnoreProperties({"affiliate"})
	@OneToOne()
	@JoinColumn(name="application_id")
	PrimaryApplication application;

	@JsonIgnoreProperties({"affiliate"})
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="affiliate")
	Set<Application> applications = Sets.newHashSet();

	@ManyToOne
	@JoinColumn(name="home_member_id")
    Member homeMember;

	@JsonIgnore
	@Field(name="homeMember")
	public String getHomeMemberKey() {
		return homeMember!= null?homeMember.getKey():null;
	}
	
	@Column(name="notes_internal")
	String notesInternal;

	public void addCommercialUsage(CommercialUsage newEntryValue) {
		Validate.notNull(newEntryValue.getCommercialUsageId());
		
		if (newEntryValue.affiliate != null) {
			newEntryValue.affiliate.commercialUsages.remove(newEntryValue);
		}
		newEntryValue.affiliate = this;
		commercialUsages.add(newEntryValue);
	}

	public Set<CommercialUsage> getCommercialUsages() {
		return Collections.unmodifiableSet(commercialUsages);
	}

	public void addApplication(Application newEntryValue) {
		Validate.notNull(newEntryValue.getApplicationId());
		
		if (newEntryValue.affiliate != null) {
			newEntryValue.affiliate.applications.remove(newEntryValue);
		}
		newEntryValue.affiliate = this;
		applications.add(newEntryValue);
	}

	public Set<Application> getApplications() {
		return Collections.unmodifiableSet(applications);
	}

	public Affiliate() {
		
	}
	
	//For Tests
	public Affiliate(Long affiliateId) {
		this.affiliateId = affiliateId;
	}
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getAffiliateId() {
		return affiliateId;
	}

	public PrimaryApplication getApplication() {
		return application;
	}

	public void setApplication(PrimaryApplication application) {
		this.application = application;
		if (application != null) {
			addApplication(application);
		}
	}
	
	public AffiliateDetails getAffiliateDetails() {
		return affiliateDetails;
	}

	public void setAffiliateDetails(AffiliateDetails affiliateDetails) {
		this.affiliateDetails = affiliateDetails;
	}

	public AffiliateType getType() {
		return type;
	}
	
	public void setType(AffiliateType type) {
		this.type = type;
	}

	public Member getHomeMember() {
		return homeMember;
	}

	public void setHomeMember(Member homeMember) {
		this.homeMember = homeMember;
	}

	public String getImportKey() {
		return importKey;
	}

	public void setImportKey(String importKey) {
		this.importKey = importKey;
	}

	@Override
	protected Object getPK() {
		return affiliateId;
	}

	public String getNotesInternal() {
		return notesInternal;
	}

	public void setNotesInternal(String notesInternal) {
		this.notesInternal = notesInternal;
	}
}
