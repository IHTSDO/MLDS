package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.joda.time.Instant;

@Entity
public class Application extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name="application_id")
    Long applicationId;
	
	String username;
	
	@Enumerated(EnumType.STRING)
	AffiliateType type;
	
	@Enumerated(EnumType.STRING)
	@Column(name="subtype")
	AffiliateSubType subType;
	
	@OneToOne()
	@JoinColumn(name="affiliate_details_id")
	AffiliateDetails affiliateDetails;
	
	@Column(name="other_text")
	String otherText;
	
	boolean snomedlicense;
	
	@Column(name="notes_internal")
	String notesInternal;
	
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
	@JoinColumn(name="commercial_usage_id")
	CommercialUsage commercialUsage;

	@ManyToOne
	@JoinColumn(name="member_id")
    Member member;

	public Application() {
		
	}
	
	// For tests
	public Application(Long applicationId) {
		this.applicationId = applicationId;
	}
	
	public AffiliateSubType getSubType() {
		return subType;
	}

	public void setSubType(AffiliateSubType subType) {
		this.subType = subType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AffiliateType getType() {
		return type;
	}

	public void setType(AffiliateType type) {
		this.type = type;
	}

	public boolean isSnoMedLicence() {
		return snomedlicense;
	}

	public void setSnoMedLicence(boolean snoMedLicence) {
		this.snomedlicense = snoMedLicence;
	}

	public String getOtherText() {
		return otherText;
	}

	public void setOtherText(String otherText) {
		this.otherText = otherText;
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

	public CommercialUsage getCommercialUsage() {
		return commercialUsage;
	}

	public void setCommercialUsage(CommercialUsage commercialUsage) {
		this.commercialUsage = commercialUsage;
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

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
