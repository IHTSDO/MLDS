package ca.intelliware.ihtsdo.mlds.domain;

public class PermissionVisibilityResponse {

    private Long releaseVersionId;
    private String versionName;
    private String permissionType;
    private String releaseType;

    public PermissionVisibilityResponse(Long releaseVersionId,
                                        String versionName,
                                        String permissionType,
                                        String releaseType) {
        this.releaseVersionId = releaseVersionId;
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

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }
}

