package ca.intelliware.ihtsdo.mlds.web.rest.dto;


import ca.intelliware.ihtsdo.mlds.domain.Member;
import com.fasterxml.jackson.annotation.JsonFilter;

import java.time.Instant;

/*
 * DTO for transporting the entire member record to/from the client as JSON with Jackson.
 *
 * Typically the app uses the Entity for that purpose, however, the Member entity
 * is referenced from many other entities and as on optimization the Member entity is
 * serialized to JSON as merely the Member key rather than the entire state.
 * This DTO is used when the client needs the entire state of a member.
 *
 * Note that the privacy filter is used to exclude private fields, such as
 * staffNotificationEmal, from being serialized to JSON.
 */

//@JsonFilter("memberDtoPrivacyFilter")
public class MemberDTO {
	public static final String[] PRIVATE_FIELDS = {"staffNotificationEmail"};

	Long memberId;
    String key;
    Instant createdAt;

    String memberOrgURL;
    String memberOrgName;
    String contactEmail;

    FileDTO license;
    String licenseName;
    String licenseVersion;

    private Boolean promotePackages;

    private String name;
    private FileDTO logo;

    // Sensitive email addresses - intent is to only reveal to other staff/admins
    private String staffNotificationEmail;


    public MemberDTO() {
    }

    public MemberDTO(Member member) {
    	this.memberId = member.getMemberId();
    	this.key = member.getKey();
    	this.createdAt = member.getCreatedAt();
    	this.promotePackages = member.getPromotePackages();
    	this.licenseName = member.getLicenseName();
    	this.licenseVersion = member.getLicenseVersion();
    	if (member.getLicense() != null) {
    		this.license = new FileDTO(member.getLicense());
    	}
    	this.name = member.getName();
    	if (member.getLogo() != null) {
    		this.logo = new FileDTO(member.getLogo());
    	}
    	this.staffNotificationEmail = member.getStaffNotificationEmail();
        this.memberOrgURL=member.getMemberOrgURL();
        this.memberOrgName=member.getMemberOrgName();
        this.contactEmail=member.getContactEmail();
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

	public FileDTO getLicense() {
		return license;
	}

	public String getLicenseName() {
		return licenseName;
	}

	public String getLicenseVersion() {
		return licenseVersion;
	}

	public String getName() {
		return name;
	}

	public FileDTO getLogo() {
		return logo;
	}

	public String getStaffNotificationEmail() {
		return staffNotificationEmail;
	}

	public Boolean getPromotePackages() {
		return promotePackages;
	}

    public String getContactEmail() { return contactEmail; }
    public String getMemberOrgName() { return memberOrgName; }
    public String getMemberOrgURL() { return memberOrgURL; }
}
