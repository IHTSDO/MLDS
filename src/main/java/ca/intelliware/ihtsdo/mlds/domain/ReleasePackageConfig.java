package ca.intelliware.ihtsdo.mlds.domain;

import jakarta.persistence.*;

@Entity
@Table(name="release_config_master")
public class ReleasePackageConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
    @SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "mlds.hibernate_sequence", allocationSize = 1)
    @Column(name="config_id")
    Long configId;

    @Column(name="release_type")
    public String releaseType;

    @Column(name="release_package")
    public String releasePackageAccess;

    @Column(name="release_permission_type")
    public String releasePermissionType;

    @Column(name = "user_list", columnDefinition = "JSON")
    private String userList;

    @Column(name = "is_active")
    private Boolean isActive;


    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getReleasePackageAccess() {
        return releasePackageAccess;
    }

    public void setReleasePackageAccess(String releasePackageAccess) {
        this.releasePackageAccess = releasePackageAccess;
    }

    public String getReleasePermissionType() {
        return releasePermissionType;
    }

    public void setReleasePermissionType(String releasePermissionType) {
        this.releasePermissionType = releasePermissionType;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
