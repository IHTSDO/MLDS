package ca.intelliware.ihtsdo.mlds.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import java.time.Instant;


@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="application_type")
// Note that concrete subclass MUST set @Where and @SQLDelete for soft-delete functionality
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE application SET inactive_at = now() WHERE application_id = ?")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=As.PROPERTY, property="applicationType")
@JsonSubTypes({
	@JsonSubTypes.Type(value=PrimaryApplication.class, name="PRIMARY"),
	@JsonSubTypes.Type(value=ExtensionApplication.class, name="EXTENSION"),
	@JsonSubTypes.Type(value=ImportApplication.class, name="IMPORT")
	})
public abstract class Application extends BaseEntity {
	public static enum ApplicationType {
		PRIMARY, EXTENSION, IMPORT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="application_id", columnDefinition = "BIGINT")
    Long applicationId;

	// the parent
	@JsonIgnoreProperties({"application", "applications"})
	@ManyToOne
	@JoinColumn(name="affiliate_id")
	Affiliate affiliate;

	String username;

	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="affiliate_details_id")
	AffiliateDetails affiliateDetails;

	@Column(name="notes_internal")
	String notesInternal;

	@Column(name="created_at")
	private
	Instant createdAt = Instant.now();

	@JsonIgnore
	@Column(name="inactive_at")
	Instant inactiveAt;

	// Timestamp last submitted by the applicant
	@Column(name="submitted_at")
	Instant submittedAt;

	// Timestamp when application was completed by staff, into either the  ACCEPTED or REJECTED state
	@Column(name="completed_at")
	Instant completedAt;

	@Enumerated(EnumType.STRING)
	@Column(name="approval_state")
	ApprovalState approvalState = ApprovalState.NOT_SUBMITTED;

	@ManyToOne
	@JoinColumn(name="member_id")
    Member member;

	@Column(name="application_type", insertable=false, updatable=false)
	String applicationTypeValue;


	public Application() {

	}

	// For tests
	public Application(Long applicationId) {
		this.applicationId = applicationId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNotesInternal() {
		return notesInternal;
	}

	public void setNotesInternal(String notesInternal) {
		this.notesInternal = notesInternal;
	}

	public Instant getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(Instant submittedAt) {
		this.submittedAt = submittedAt;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}

	public ApprovalState getApprovalState() {
		return approvalState;
	}

	public void setApprovalState(ApprovalState approvalState) {
		this.approvalState = approvalState;
	}

	public Long getApplicationId() {
		return applicationId;
	}

	@Override
	protected Object getPK() {
		return applicationId;
	}

	public AffiliateDetails getAffiliateDetails() {
		return affiliateDetails;
	}

	public void setAffiliateDetails(AffiliateDetails affiliateDetails) {
		this.affiliateDetails = affiliateDetails;
	}

	public Affiliate getAffiliate() {
		return affiliate;
	}

	public void setAffiliate(Affiliate affiliate) {
		affiliate.addApplication(this);
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public static Application create(ApplicationType applicationType) {
		switch (applicationType) {
		case PRIMARY:
			return new PrimaryApplication();

		case EXTENSION:
			return new ExtensionApplication();

		case IMPORT:
			return new ImportApplication();

		default:
			throw new RuntimeException("Unsupported applicationType " + applicationType);
		}
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	@JsonIgnore
	public abstract ApplicationType getApplicationType();

	@JsonIgnore
	public String getApplicationTypeValue() {
		return applicationTypeValue;
	}

	public Instant getInactiveAt() {
		return inactiveAt;
	}

	public void setInactiveAt(Instant inactiveAt) {
		this.inactiveAt = inactiveAt;
	};

}
