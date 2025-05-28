package ca.intelliware.ihtsdo.mlds.web.rest.dto;

import ca.intelliware.ihtsdo.mlds.domain.ReleasePermissionType;

public class ReleasePermissionRequestDTO {
    private Long releasePackageId;
    private String name;
    private ReleasePermissionType permissionType;

    public ReleasePermissionRequestDTO(Long releasePackageId, String name, ReleasePermissionType permissionType) {
        this.releasePackageId = releasePackageId;
        this.name = name;
        this.permissionType = permissionType;
    }

    public Long getReleasePackageId() {
        return releasePackageId;
    }

    public void setReleasePackageId(Long releasePackageId) {
        this.releasePackageId = releasePackageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReleasePermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(ReleasePermissionType permissionType) {
        this.permissionType = permissionType;
    }
}
