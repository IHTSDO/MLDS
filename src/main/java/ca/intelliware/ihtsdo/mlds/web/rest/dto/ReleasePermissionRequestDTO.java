package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType;

public class ReleasePermissionRequestDTO {
    private Long releaseVersionId;
    private String packageName;
    private String versionName;
    private ReleasePermissionType permissionType;
    private String releaseType;

    public ReleasePermissionRequestDTO(Long releaseVersionId,
                                       String packageName,
                                       String versionName,
                                       ReleasePermissionType permissionType, String releaseType) {

        this.releaseVersionId = releaseVersionId;
        this.packageName = packageName;
        this.versionName = versionName;
        this.permissionType = permissionType;
        this.releaseType = releaseType;
    }

    public Long getReleaseVersionId() {
        return releaseVersionId;
    }

    public void setReleaseVersionId(Long releaseVersionId) {
        this.releaseVersionId = releaseVersionId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public ReleasePermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(ReleasePermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }
}
