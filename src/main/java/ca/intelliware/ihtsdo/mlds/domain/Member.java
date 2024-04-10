package ca.intelliware.ihtsdo.mlds.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.Instant;

@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Member extends BaseEntity {
	public static final String KEY_IHTSDO = "IHTSDO";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_demo")
	@SequenceGenerator(name = "hibernate_demo", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
	@Column(name="member_id")
	Long memberId;
    @Column(name="`key`")
    String key;

    @Column(name="created_at")
	Instant createdAt = Instant.now();

    @Column(name="contactEmail")
    public String contactEmail;

    @Column(name="memberOrgName")
    public String memberOrgName;

    @Column(name="memberOrgURL")
    public String memberOrgURL;

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

    @Column(name="staff_notification_email")
    private String staffNotificationEmail;

    @Column(name="promote_packages")
    private Boolean promotePackages;

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
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
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

	public String getStaffNotificationEmail() {
		return staffNotificationEmail;
	}

	public void setStaffNotificationEmail(String staffNotificationEmail) {
		this.staffNotificationEmail = staffNotificationEmail;
	}

	public Boolean getPromotePackages() {
		return promotePackages;
	}

	public void setPromotePackages(Boolean promotePackages) {
		this.promotePackages = promotePackages;
	}

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail)
    { this.contactEmail = contactEmail; }
    public String getMemberOrgName() { return memberOrgName; }
    public void setMemberOrgName(String memberOrgName) { this.memberOrgName = memberOrgName; }
    public String getMemberOrgURL() { return memberOrgURL; }
    public void setMemberOrgURL(String memberOrgURL) { this.memberOrgURL = memberOrgURL; }

}
