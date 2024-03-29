package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.apache.lucene.analysis.standard.UAX29URLEmailAnalyzer;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.intelliware.ihtsdo.mlds.search.AffiliateTypeFieldBridge;
import ca.intelliware.ihtsdo.mlds.search.OrganizationTypeFieldBridge;


@Entity
@Table(name="affiliate_details")
@Where(clause = "inactive_at IS NULL")
@SQLDelete(sql="UPDATE affiliate_details SET inactive_at = now() WHERE affiliate_details_id = ?")
public class AffiliateDetails extends BaseEntity implements Cloneable {

	@Id
	@GeneratedValue
	@Column(name="affiliate_details_id")
    Long affiliateDetailsId;

	@JsonIgnore
	@Column(name="inactive_at")
	private
	Instant inactiveAt;

	@Enumerated(EnumType.STRING)
	@Field(name="ALL",bridge=@FieldBridge(impl=AffiliateTypeFieldBridge.class))
	AffiliateType type;

	/** text for "Other" AffiliateType */
	@Column(name = "other_text")
	String otherText;

	@Enumerated(EnumType.STRING)
	@Column(name = "subtype")
	AffiliateSubType subType;

	@Enumerated(EnumType.STRING)
	@Column(name="agreement_type")
	AgreementType agreementType = AgreementType.AFFILIATE_NORMAL;

	@Column(name="first_name")
	@Fields({ @Field(name="ALL"), @Field()})
	String firstName;

	@Column(name="last_name")
	@Fields({ @Field(name="ALL"), @Field()})
	String lastName;

	@Fields({
		@Field(name="ALL"),  // the default analyzer splits on @
		@Field(analyzer=@Analyzer(impl=UAX29URLEmailAnalyzer.class))
		})
	String email;

	@Column(name="alternate_email")
	@Fields({
		@Field(name="ALL"),  // the default analyzer splits on @
		@Field(name="email",analyzer=@Analyzer(impl=UAX29URLEmailAnalyzer.class))
		})
	String alternateEmail;

	@Column(name="third_email")
	@Fields({
		@Field(name="ALL"),  // the default analyzer splits on @
		@Field(name="email",analyzer=@Analyzer(impl=UAX29URLEmailAnalyzer.class))
		})
	String thirdEmail;

	@Column(name="landline_number")
	String landlineNumber;
	@Column(name="landline_extension")
	String landlineExtension;
	@Column(name="mobile_number")
	String mobileNumber;

	@Enumerated(EnumType.STRING)
	@Column(name="organization_type")
	@Field(name="ALL",bridge=@FieldBridge(impl=OrganizationTypeFieldBridge.class))
	OrganizationType organizationType;

	@Column(name="organization_type_other")
	String organizationTypeOther;

	@AttributeOverrides({
		@AttributeOverride(name="street", column=@Column(name="street")),
		@AttributeOverride(name="city", column=@Column(name="city")),
		@AttributeOverride(name="post", column=@Column(name="post"))
	})
	@AssociationOverrides(@AssociationOverride(name="country", joinColumns=@JoinColumn(name="country_iso_code_2")))
	@Embedded
	@IndexedEmbedded
	MailingAddress address;

	@Column(name="organization_name")
	@Fields({ @Field(name="ALL"), @Field()})
	String organizationName;

	@AttributeOverrides({
		@AttributeOverride(name="street", column=@Column(name="billing_street")),
		@AttributeOverride(name="city", column=@Column(name="billing_city")),
		@AttributeOverride(name="post", column=@Column(name="billing_post"))
	})
	@AssociationOverrides(@AssociationOverride(name="country", joinColumns=@JoinColumn(name="billing_country_iso_code_2")))
	@Embedded
	@IndexedEmbedded
	MailingAddress billingAddress;

	transient boolean acceptNotifications = true;

    transient boolean countryNotificationsOnly = false;

	public boolean isAcceptNotifications() {
		return acceptNotifications;
	}

	public void setAcceptNotifications(boolean acceptNotifications) {
		this.acceptNotifications = acceptNotifications;
	}

    public boolean isCountryNotificationsOnly() {
        return countryNotificationsOnly;
    }

    public void setCountryNotificationsOnly(boolean countryNotificationsOnly) {
        this.countryNotificationsOnly = countryNotificationsOnly;
    }

    public Long getAffiliateDetailsId() {
		return affiliateDetailsId;
	}

	public void setAffiliateDetailsId(Long affiliateDetailsId) {
		this.affiliateDetailsId = affiliateDetailsId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlternateEmail() {
		return alternateEmail;
	}

	public void setAlternateEmail(String alternateEmail) {
		this.alternateEmail = alternateEmail;
	}

	public String getThirdEmail() {
		return thirdEmail;
	}

	public void setThirdEmail(String thirdEmail) {
		this.thirdEmail = thirdEmail;
	}

	public MailingAddress getAddress() {
		return address;
	}

	public void setAddress(MailingAddress address) {
		this.address = address;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public MailingAddress getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(
			MailingAddress organizationBillingAddress) {
		this.billingAddress = organizationBillingAddress;
	}

	@Override
	protected Object getPK() {
		return affiliateDetailsId;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public String getLandlineExtension() {
		return landlineExtension;
	}

	public void setLandlineExtension(String landlineExtension) {
		this.landlineExtension = landlineExtension;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public OrganizationType getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(OrganizationType organizationType) {
		this.organizationType = organizationType;
	}

	public String getOrganizationTypeOther() {
		return organizationTypeOther;
	}

	public void setOrganizationTypeOther(String organizationTypeOther) {
		this.organizationTypeOther = organizationTypeOther;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public AffiliateDetails copyNoId() {
		AffiliateDetails detailsCopy = (AffiliateDetails) clone();
		detailsCopy.setAffiliateDetailsId(null);
		detailsCopy.setAddress((MailingAddress) address.clone());
		detailsCopy.setBillingAddress((MailingAddress) billingAddress.clone());
		return detailsCopy;
	}

	public AffiliateType getType() {
		return type;
	}

	public void setType(AffiliateType type) {
		this.type = type;
	}

	public String getOtherText() {
		return otherText;
	}

	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}

	public AffiliateSubType getSubType() {
		return subType;
	}

	public void setSubType(AffiliateSubType subType) {
		this.subType = subType;
	}

	public AgreementType getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(AgreementType agreementType) {
		this.agreementType = agreementType;
	}
}
