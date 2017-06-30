package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Field;

@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Country extends BaseEntity {

    @Id
    @Column(name="iso_code_2")
    private String isoCode2;
    
    @Column(name="iso_code_3")
    private String isoCode3;
    
    @Field
    @Column(name="common_name")
    private String commonName;
    
    private boolean excludeUsage;
    
    @Column(name="alternate_registration_url")
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

	public boolean isExcludeUsage() {
		return excludeUsage;
	}

	public void setExcludeUsage(boolean excludeUsage) {
		this.excludeUsage = excludeUsage;
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
