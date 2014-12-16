package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Member extends BaseEntity {
	public static final String KEY_IHTSDO = "IHTSDO";
	
	@Id
	@GeneratedValue
	@Column(name="member_id")
	Long memberId;

    String key;

    @Column(name="created_at")
    Instant createdAt = Instant.now();
    
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="license_file")
    File licenseFile;
    
    @Column(name="license_name")
    String licenseName;
    
    @Column(name="license_version")
    String licenseVersion;
    
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="logo_file")
	private
    File logoFile;    
    
    private String name;
    
	public Member() {}
	
	public Member(String key, long memberId) {
		this.key = key;
		this.memberId = memberId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public String getKey() {
		return key;
	}

	/**
	 * Setter for Jackson
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}
	
	public Instant getCreatedAt() {
		return createdAt;
	}

	@Override
	protected Object getPK() {
		return memberId;
	}

	@JsonIgnore
	public File getLicense() {
		return licenseFile;
	}

	public void setLicense(File license) {
		this.licenseFile = license;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE)
			.append("key", key)
			.append("memberId", memberId)
			.toString();
	}

	public String getLicenseName() {
		return licenseName;
	}

	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}

	public String getLicenseVersion() {
		return licenseVersion;
	}

	public void setLicenseVersion(String licenseVersion) {
		this.licenseVersion = licenseVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public File getLogo() {
		return logoFile;
	}

	public void setLogo(File logoFile) {
		this.logoFile = logoFile;
	}
    
}
