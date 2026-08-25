package ca.intelliware.ihtsdo.mlds.domain;


import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class MailingAddress {


	String street;


	String city;


	@ManyToOne
	Country country;

	String post;

	public MailingAddress() {
	}

	public MailingAddress(MailingAddress other) {
		if (other != null) {
			this.street = other.street;
			this.city = other.city;
			this.country = other.country;
			this.post = other.post;
		}
	}

	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Country getCountry() {
		return country;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
}
