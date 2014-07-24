package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="affiliate_details")
public class AffiliateDetails {
	
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
	
	@OneToOne
	@JoinColumn(name="address_id")
	MailingAddress address;
	
	@Column(name="organization_name")
	String organizationName;
	
	@OneToOne()
	@JoinColumn(name="organization_address_id")
	MailingAddress organizationAddress;
	
	@OneToOne()
	@JoinColumn(name="organization_billing_address_id")
	MailingAddress organizationBillingAddress;

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

	public MailingAddress getOrganizationAddress() {
		return organizationAddress;
	}

	public void setOrganizationAddress(MailingAddress organizationAddress) {
		this.organizationAddress = organizationAddress;
	}

	public MailingAddress getOrganizationBillingAddress() {
		return organizationBillingAddress;
	}

	public void setOrganizationBillingAddress(
			MailingAddress organizationBillingAddress) {
		this.organizationBillingAddress = organizationBillingAddress;
	}
	
	
}
