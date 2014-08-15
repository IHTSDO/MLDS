package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Country extends BaseEntity {

    @Id
    @Column(name="iso_code_2")
    private String isoCode2;
    
    @Column(name="iso_code_3")
    private String isoCode3;
    
    @Column(name="common_name")
    private String commonName;
    
    private boolean excludeRegistration;
    
    private String alternateRegistrationUrl;
    
	@ManyToOne
	@JoinColumn(name="member_id")
    private Member member;

    /* for hibernate */
	protected Country() {
	}
	
	public Country(String isoCode2, String isoCode3, String commonName) {
		super();
		this.isoCode2 = isoCode2;
		this.isoCode3 = isoCode3;
		this.commonName = commonName;
	}

	public String getIsoCode2() {
		return isoCode2;
	}

	public String getIsoCode3() {
		return isoCode3;
	}

	public String getCommonName() {
		return commonName;
	}

	public boolean isExcludeRegistration() {
		return excludeRegistration;
	}

	public void setExcludeRegistration(boolean excludeRegistration) {
		this.excludeRegistration = excludeRegistration;
	}

	public String getAlternateRegistrationUrl() {
		return alternateRegistrationUrl;
	}

	public void setAlternateRegistrationUrl(String alternateRegistrationUrl) {
		this.alternateRegistrationUrl = alternateRegistrationUrl;
	}

	@Override
	protected Object getPK() {
		return isoCode2;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
