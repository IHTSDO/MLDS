package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import ca.intelliware.ihtsdo.mlds.domain.Member;
import com.fasterxml.jackson.annotation.JsonFilter;

import java.time.Instant;

/*
 * DTO for transporting the entire member record to/from the client as JSON with Jackson.
 *
 * Typically the app uses the Entity for that purpose, however, the Member entity
 * is referenced from many other entities and as an optimization the Member entity is
 * serialized to JSON as merely the Member key rather than the entire state.
 * This DTO is used when the client needs the entire state of a member.
 *
 * The privacy filter is used to exclude private fields, such as
 * staffNotificationEmail, from being serialized to JSON.
 */
@JsonFilter("memberDtoPrivacyFilter")
public class MemberDTO {

    protected static final String[] PRIVATE_FIELDS = {
        "staffNotificationEmail"
    };

    public static String[] getPrivateFields() {
        return PRIVATE_FIELDS.clone();
    }
    private Long memberId;
    private String key;
    private Instant createdAt;

    private String memberOrgURL;
    private String memberOrgName;
    private String contactEmail;

    private FileDTO license;
    private String licenseName;
    private String licenseVersion;

    private Boolean promotePackages;

    private String name;
    private FileDTO logo;

    private String language;
    private Boolean footerActive;

    // Sensitive email address - intended to be revealed only to staff/admins
    private String staffNotificationEmail;
    private Boolean memberActive;
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
        this.memberOrgURL = member.getMemberOrgURL();
        this.memberOrgName = member.getMemberOrgName();
        this.contactEmail = member.getContactEmail();
        this.language = member.getLanguage();
        this.footerActive = member.getFooterActive();
        this.memberActive = member.getMemberActive();
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

    public String getContactEmail() {
        return contactEmail;
    }

    public String getMemberOrgName() {
        return memberOrgName;
    }

    public String getMemberOrgURL() {
        return memberOrgURL;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getFooterActive() {
        return footerActive;
    }

    public void setFooterActive(Boolean footerActive) {
        this.footerActive = footerActive;
    }
    public Boolean getMemberActive() {
        return memberActive;
    }

    public void setMemberActive(Boolean memberActive) {
        this.memberActive = memberActive;
    }

}
