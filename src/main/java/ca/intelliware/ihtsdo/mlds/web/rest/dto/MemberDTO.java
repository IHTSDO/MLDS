package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import org.joda.time.Instant;

import ca.intelliware.ihtsdo.mlds.domain.Member;

public class MemberDTO {
	Long memberId;

    String key;

    Instant createdAt;

    FileDTO licence; 

    public MemberDTO() {
    }
    
    public MemberDTO(Member member) {
    	this.memberId = member.getMemberId();
    	this.key = member.getKey();
    	this.createdAt = member.getCreatedAt();
    	if (member.getLicense() != null) {
    		this.licence = new FileDTO(member.getLicense());
    	}
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

	public FileDTO getLicence() {
		return licence;
	}
}
