package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Member extends BaseEntity {
	public static final String KEY_IHTSDO = "IHTSDO";
	
	@Id
	@GeneratedValue
	@Column(name="member_id")
	Long memberId;

    String key;

    @Column(name="created_at")
    Instant createdAt = Instant.now();
    
    @OneToOne()
	@JoinColumn(name="license_file")
    File licenseFile;
    
	public Member() {}
	
	public Member(String key) {
		this.key = key;
	}

	public Long getMemberId() {
		return memberId;
	}

	public String getKey() {
		return key;
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

    
}
