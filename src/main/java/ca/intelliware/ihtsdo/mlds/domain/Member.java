package ca.intelliware.ihtsdo.mlds.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.joda.time.Instant;

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

    
}
