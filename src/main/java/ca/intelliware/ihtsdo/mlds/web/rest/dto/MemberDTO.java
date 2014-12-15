package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import org.joda.time.Instant;

import ca.intelliware.ihtsdo.mlds.domain.Member;

public class MemberDTO {
	Long memberId;
    String key;
    Instant createdAt;

    FileDTO licence;
    String licenceName;
    String licenceVersion;
    
    private String name;
    private FileDTO logo;

    public MemberDTO() {
    }
    
    public MemberDTO(Member member) {
    	this.memberId = member.getMemberId();
    	this.key = member.getKey();
    	this.createdAt = member.getCreatedAt();
    	this.licenceName = member.getLicenceName();
    	this.licenceVersion = member.getLicenceVersion();
    	if (member.getLicense() != null) {
    		this.licence = new FileDTO(member.getLicense());
    	}
    	this.name = member.getName();
    	if (member.getLogo() != null) {
    		this.logo = new FileDTO(member.getLogo());
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

	public String getLicenceName() {
		return licenceName;
	}

	public String getLicenceVersion() {
		return licenceVersion;
	}

	public String getName() {
		return name;
	}

	public FileDTO getLogo() {
		return logo;
	}
}
