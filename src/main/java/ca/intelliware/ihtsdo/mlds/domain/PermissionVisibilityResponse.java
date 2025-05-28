package ca.intelliware.ihtsdo.mlds.domain;

public class PermissionVisibilityResponse {

    private boolean isMasterPermission;
    private String permissionType;
    private String releaseType;

    // Constructor
    public PermissionVisibilityResponse(boolean isMasterPermission, String permissionType, String releaseType) {
        this.isMasterPermission = isMasterPermission;
        this.permissionType = permissionType;
        this.releaseType = releaseType;
    }

    // Getters and Setters
    public boolean isMasterPermission() {
        return isMasterPermission;
    }

    public void setMasterPermission(boolean isMasterPermission) {
        this.isMasterPermission = isMasterPermission;
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

