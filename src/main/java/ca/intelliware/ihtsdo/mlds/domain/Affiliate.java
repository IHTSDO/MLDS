package ca.intelliware.ihtsdo.mlds.domain;




import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;
import jakarta.persistence.*;
import org.apache.commons.lang3.Validate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;


import java.time.Instant;
import java.util.Collections;
import java.util.Set;


@Entity
@Indexed
//@JsonFilter("affiliatePrivacyFilter")
//@Where(clause = "inactive_at IS NULL")
//@SQLDelete(sql="UPDATE affiliate SET inactive_at = now() WHERE affiliate_id = ?")
public class Affiliate extends BaseEntity {
	public static final String[] PRIVATE_FIELDS = {"notesInternal"};

	@Id
//	@GeneratedValue
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_demo")
	@SequenceGenerator(name = "hibernate_demo", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="affiliate_id")
//	@GenericField(searchable = Searchable.YES)
	Long affiliateId;

	Instant created = Instant.now();

	@JsonIgnore
	@Column(name="inactive_at")
	private
	Instant inactiveAt;

	@Enumerated(EnumType.STRING)
	AffiliateType type;

	String creator;

	@Column(name="import_key")
	String importKey;

	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="affiliate")
	Set<CommercialUsage> commercialUsages = Sets.newHashSet();

	@OneToOne
	@JoinColumn(name="affiliate_details_id")
	@IndexedEmbedded
	AffiliateDetails affiliateDetails;

	@JsonIgnoreProperties({"affiliate"})
	@OneToOne()
	@JoinColumn(name="application_id")
	@IndexedEmbedded
	PrimaryApplication application;

	@JsonIgnoreProperties({"affiliate"})
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="affiliate")
	Set<Application> applications = Sets.newHashSet();

	@ManyToOne
	@JoinColumn(name="home_member_id")
    Member homeMember;

	@JsonIgnore
//	@Field(name="homeMember", analyzer=@Analyzer(impl= KeywordAnalyzer.class))
	public String getHomeMemberKey() {
		return homeMember!= null?homeMember.getKey():null;
	}

	@Column(name="notes_internal")
	String notesInternal;

    @OneToOne()
    @JoinColumn(name="affiliate_id")
//    @IndexedEmbedded  //(prefix="")
	@IndexedEmbedded
    ViewAffiliateApplicationCountries approvedCountries;

//    @Fields({
//		@Field(name="standingState",bridge=@FieldBridge(impl= StandingStateFieldBridge.class)),
//		@Field(name="ALL",bridge=@FieldBridge(impl= StandingStateFieldBridge.class))
//	})
	@Enumerated(EnumType.STRING)
	@Column(name="standing_state")
	StandingState standingState = StandingState.APPLYING;

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

	public StandingState getStandingState() {
		return standingState;
	}

	public void setStandingState(StandingState standingState) {
		this.standingState = standingState;
	}

	public Instant getInactiveAt() {
		return inactiveAt;
	}

	public void setInactiveAt(Instant inactiveAt) {
		this.inactiveAt = inactiveAt;
	}
}
