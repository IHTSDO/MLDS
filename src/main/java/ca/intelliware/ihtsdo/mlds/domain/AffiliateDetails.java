package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;


@Entity
@Table(name="affiliate_details")
public class AffiliateDetails extends BaseEntity {
	
	@Id
	@GeneratedValue
	@Column(name="affiliate_details_id")
    Long affiliateDetailsId;
		
	@Column(name="first_name")
	String firstName;
	
	@Column(name="last_name")
	String lastName;
	
	String email;
	
	@Column(name="alternate_email")
	String alternateEmail;
	
	@Column(name="third_email")
	String thirdEmail;
	
	@AttributeOverrides({
		@AttributeOverride(name="street", column=@Column(name="street")),
		@AttributeOverride(name="city", column=@Column(name="city")),
		@AttributeOverride(name="post", column=@Column(name="post"))
	})
	@AssociationOverrides(@AssociationOverride(name="country", joinColumns=@JoinColumn(name="country_iso_code_2")))
	@Embedded
	MailingAddress address;
	
	@Column(name="organization_name")
	String organizationName;
	
	@AttributeOverrides({
		@AttributeOverride(name="street", column=@Column(name="billing_street")),
		@AttributeOverride(name="city", column=@Column(name="billing_city")),
		@AttributeOverride(name="post", column=@Column(name="billing_post"))
	})
	@AssociationOverrides(@AssociationOverride(name="country", joinColumns=@JoinColumn(name="billing_country_iso_code_2")))
	@Embedded
	MailingAddress billingAddress;

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
	
	
}
