package ca.intelliware.ihtsdo.mlds.domain;


import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class MailingAddress implements Cloneable {


	String street;


	String city;


	@ManyToOne
	Country country;

	String post;

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

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// we support clone
			throw new RuntimeException(e);
		}
	}
}
