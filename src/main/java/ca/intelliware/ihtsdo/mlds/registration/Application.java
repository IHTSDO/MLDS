package ca.intelliware.ihtsdo.mlds.registration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Application {
	@Id
	@GeneratedValue
	@Column(name="application_id")
    private Long applicationId;
	
	String username;
	boolean approved;
	
	String type;
	@Column(name="subtype")
	String subType;
	
	@Column(name="full_name")
	String fullName;
	String email;
	@Column(name="alternate_email")
	String alternateEmail;
	@Column(name="third_email")
	String thirdEmail;
	@Column(name="landline_number")
	String landlineNumber;
	@Column(name="landline_extension")
	String landlineExtension;
	@Column(name="mobile_number")
	String mobileNumber;
	
	@Column(name="organization_name")
	String organizationName;
	@Column(name="organization_type")
	String organizationType;
	@Column(name="organization_type_other")
	String organizationTypeOther;
	
	String street;
	String city;
	@Column(name="post_code")
	String postCode;
	String country;
	
	@Column(name="billing_street")
	String billingStreet;
	@Column(name="billing_city")
	String billingCity;
	@Column(name="billing_post_code")
	String billingPostCode;

	@Column(name="billing_country")
	String billingCountry;
	
	@Column(name="other_text")
	String otherText;
	
	boolean snomedlicense;
	
	@Column(name="is_submitted")
	boolean isSubmitted;

	
	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}


	public boolean isApproved() {
		return approved;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return fullName;
	}

	public void setName(String name) {
		this.fullName = name;
	}

	public String getAddress() {
		return street;
	}

	public void setAddress(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhoneNumber() {
		return landlineNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.landlineNumber = phoneNumber;
	}

	public String getExtension() {
		return landlineExtension;
	}

	public void setExtension(String extension) {
		this.landlineExtension = extension;
	}

	public boolean isSnoMedLicence() {
		return snomedlicense;
	}

	public void setSnoMedLicence(boolean snoMedLicence) {
		this.snomedlicense = snoMedLicence;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getBillingCountry() {
		return billingCountry;
	}

	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingStreet() {
		return billingStreet;
	}

	public void setBillingStreet(String billingStreet) {
		this.billingStreet = billingStreet;
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

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getOtherText() {
		return otherText;
	}

	public void setOtherText(String otherText) {
		this.otherText = otherText;
	}

	public void resetStatus() {
		this.isSubmitted = false;
	}

	public void setStatus() {
		this.isSubmitted = true;
	}
	
	public boolean isSubmitted() {
		return this.isSubmitted;
	}

	public String getPostCode() {
		return this.postCode;
	}
	
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getBillingPostCode() {
		return billingPostCode;
	}

	public void setBillingPostCode(String billingPostCode) {
		this.billingPostCode = billingPostCode;
	}

	public String getOrganizationTypeOther() {
		return organizationTypeOther;
	}

	public void setOrganizationTypeOther(String organizationTypeOther) {
		this.organizationTypeOther = organizationTypeOther;
	}

}
